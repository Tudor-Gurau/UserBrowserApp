package com.example.userbrowserapp.data.models

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.userbrowserapp.domain.model.UserModel

@Dao
interface UserDAO {

    @Query("SELECT * FROM bookmarked_users")
    fun getBookmarkedUsers(): List<UserModel>

    @Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    fun bookmarkUser(user: UserModel)

    @Query("DELETE FROM bookmarked_users WHERE userId = :userId")
    fun deleteBookmarkedUser(userId: String)
}