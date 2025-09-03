package com.example.userbrowserapp.data.repository

import com.example.userbrowserapp.data.models.UserDatabase
import com.example.userbrowserapp.data.remote.RandomUserApi
import com.example.userbrowserapp.domain.model.UserModel
import com.example.userbrowserapp.domain.repository.RandomUserListRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RandomUserListRepositoryImpl @Inject constructor(
    private val api: RandomUserApi,
    private val database: UserDatabase,
) : RandomUserListRepository {

    override suspend fun getUsers(page: Int) : List<UserModel> {
        val lisOfUsers = mutableListOf<UserModel>()
        val response = api.getUsers(page = page)
        val bookmarkedIds = database.userDao().getBookmarkedUsers().map { it.userId }.toSet()
        for (user in response.results) {
            val userModel = UserModel(
                userId = user.login.uuid,
                name = "${user.name.first} ${user.name.last}",
                email = user.email,
                phone = user.phone,
                picture = user.picture.thumbnail,
                isBookmarked = bookmarkedIds.contains(user.login.uuid),
            )
            lisOfUsers.add(userModel)
        }
        return lisOfUsers
    }

    override suspend fun getBookmarkedUsers(): List<UserModel> {
        return database.userDao().getBookmarkedUsers()
    }

    override suspend fun bookmarkUser(user: UserModel) {
        return database.userDao().bookmarkUser(user)
    }

    override suspend fun deleteBookmarkedUser(userId: String) {
        database.userDao().deleteBookmarkedUser(userId)
    }
}