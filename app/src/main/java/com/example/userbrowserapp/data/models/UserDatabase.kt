package com.example.userbrowserapp.data.models

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.userbrowserapp.domain.model.UserModel

@Database(
    entities = [UserModel::class],
    version = 2,
    exportSchema = false
)
abstract class UserDatabase : RoomDatabase() {
    abstract fun userDao(): UserDAO

    companion object {
        const val DATABASE_NAME = "user_db"
    }
}