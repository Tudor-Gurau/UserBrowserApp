package com.example.userbrowserapp.domain.repository

import com.example.userbrowserapp.domain.model.UserModel

interface RandomUserListRepository {
    suspend fun getUsers(page: Int) : List<UserModel>

    suspend fun getBookmarkedUsers() : List<UserModel>

    suspend fun bookmarkUser(user: UserModel)

    suspend fun deleteBookmarkedUser(userId: String)
}