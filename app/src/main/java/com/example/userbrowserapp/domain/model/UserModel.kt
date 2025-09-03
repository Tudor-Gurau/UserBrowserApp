package com.example.userbrowserapp.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "bookmarked_users"
)

data class UserModel(
    @PrimaryKey(
        autoGenerate = false
    ) val id: Int? = 1,
    val userId: String,
    val name: String,
    val email: String,
    val phone: String,
    val picture: String,
    val isBookmarked: Boolean = false)