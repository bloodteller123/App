package com.example.androidapplicationproject.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_table")
data class UserTable(
    @PrimaryKey(autoGenerate = true)
//    @PrimaryKey
    val userId: Int = 0,
    val password: String,
    val userName: String,
//    val firstName: String,
//    val lastName: String,
    val email: String
)