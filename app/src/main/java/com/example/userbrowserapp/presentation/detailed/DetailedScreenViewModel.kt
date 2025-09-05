package com.example.userbrowserapp.presentation.detailed

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.userbrowserapp.domain.model.UserModel
import com.example.userbrowserapp.domain.repository.RandomUserListRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailedScreenViewModel @Inject constructor(
    private val repository: RandomUserListRepository
) : ViewModel() {

    private val _user = MutableStateFlow<UserModel?>(null)
    val user: StateFlow<UserModel?> = _user.asStateFlow()

    fun setUser(user: UserModel) {
        _user.value = user
    }

    fun toggleBookmark() {
        val currentUser = _user.value ?: return
        
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (currentUser.isBookmarked) {
                    repository.deleteBookmarkedUser(currentUser.userId)
                    _user.value = currentUser.copy(isBookmarked = false)
                } else {
                    repository.bookmarkUser(currentUser.copy(isBookmarked = true))
                    _user.value = currentUser.copy(isBookmarked = true)
                }
            } catch (e: Exception) {
                Log.d("toggleBookmark", e.toString())
            }
        }
    }
}
