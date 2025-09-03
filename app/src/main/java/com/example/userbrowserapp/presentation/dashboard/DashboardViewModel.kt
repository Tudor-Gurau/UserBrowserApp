package com.example.userbrowserapp.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.userbrowserapp.domain.model.UserModel
import com.example.userbrowserapp.domain.repository.RandomUserListRepository
import com.example.userbrowserapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: RandomUserListRepository
) : ViewModel() {

    private val _usersState = MutableStateFlow<Resource<List<UserModel>>>(Resource.Loading())
    val usersState: StateFlow<Resource<List<UserModel>>> = _usersState.asStateFlow()

    private val _bookmarkedUsers = MutableStateFlow<List<UserModel>>(emptyList())
    val bookmarkedUsers: StateFlow<List<UserModel>> = _bookmarkedUsers.asStateFlow()

    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore.asStateFlow()

    private var currentPage = 1
    private var hasMorePages = true
    private val allUsers = mutableListOf<UserModel>()

    init {
        loadInitialUsers()
        loadBookmarkedUsers()
    }

    private fun loadInitialUsers() {
        viewModelScope.launch {
            try {
                val initialUsers = repository.getUsers(1)
                allUsers.clear()
                allUsers.addAll(initialUsers)
                currentPage = 2 // Set to 2 for next page
                hasMorePages = initialUsers.isNotEmpty()
                _usersState.value = Resource.Success(allUsers.toList())
            } catch (e: Exception) {
                _usersState.value = Resource.Error(e.message ?: "An error occurred")
            }
        }
    }

    fun loadMoreUsers() {
        if (_isLoadingMore.value || !hasMorePages) return
        
        viewModelScope.launch {
            _isLoadingMore.value = true
            
            try {
                val newUsers = repository.getUsers(currentPage)
                
                if (newUsers.isEmpty()) {
                    hasMorePages = false
                } else {
                    allUsers.addAll(newUsers)
                    currentPage++
                    _usersState.value = Resource.Success(allUsers.toList())
                }
            } catch (e: Exception) {
                _usersState.value = Resource.Error(e.message ?: "An error occurred")
            } finally {
                _isLoadingMore.value = false
            }
        }
    }

    fun loadBookmarkedUsers() {
        viewModelScope.launch {
            try {
                val bookmarked = repository.getBookmarkedUsers()
                _bookmarkedUsers.value = bookmarked
            } catch (e: Exception) {
                // Handle error silently for bookmarked users
            }
        }
    }

    fun toggleBookmark(user: UserModel) {
        viewModelScope.launch {
            try {
                if (user.isBookmarked) {
                    repository.deleteBookmarkedUser(user.userId)
                } else {
                    repository.bookmarkUser(user.copy(isBookmarked = true))
                }
                loadBookmarkedUsers()
                
                // Update the user in the main list
                val userIndex = allUsers.indexOfFirst { it.userId == user.userId }
                if (userIndex != -1) {
                    allUsers[userIndex] = allUsers[userIndex].copy(isBookmarked = !allUsers[userIndex].isBookmarked)
                    _usersState.value = Resource.Success(allUsers.toList())
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun refreshUsers() {
        currentPage = 1
        hasMorePages = true
        allUsers.clear()
        _usersState.value = Resource.Loading()
        loadInitialUsers()
    }

    fun refreshAllData() {
        loadBookmarkedUsers()
        // Also refresh the main list to reflect bookmark changes
        viewModelScope.launch {
            try {
                val bookmarkedIds = repository.getBookmarkedUsers().map { it.userId }.toSet()
                allUsers.forEachIndexed { index, user ->
                    allUsers[index] = user.copy(isBookmarked = bookmarkedIds.contains(user.userId))
                }
                _usersState.value = Resource.Success(allUsers.toList())
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
