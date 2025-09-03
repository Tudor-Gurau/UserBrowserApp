package com.example.userbrowserapp.data.models

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.userbrowserapp.domain.model.UserModel

@Dao
interface UserDAO {

    @Query("SELECT * FROM bookmarked_users")
    suspend fun getBookmarkedUsers(): List<UserModel>

    @Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun bookmarkUser(user: UserModel)

    @Query("DELETE FROM bookmarked_users WHERE userId = :userId")
    suspend fun deleteBookmarkedUser(userId: String)
}