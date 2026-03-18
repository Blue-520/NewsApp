package com.blue.newsapp.data.loacl.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "users", indices = [Index(value = ["username"], unique = true)])
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id : Long = 0L,

    val username: String,

    val password: String
)