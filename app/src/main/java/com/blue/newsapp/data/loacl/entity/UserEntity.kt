package com.blue.newsapp.data.loacl.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "users", indices = [Index(value = ["username"], unique = true)])
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id : Long = 0L,

    val username: String,

    val password: String,

    val avatar: String = "",

    val backgroundImage: String = "",

    val signature: String = "这个人很懒，但会认真读每一条新闻",

    // ===== 兴趣分数 =====
    val businessScore: Int = 0,
    val entertainmentScore: Int = 0,
    val healthScore: Int = 0,
    val scienceScore: Int = 0,
    val sportsScore: Int = 0,
    val technologyScore: Int = 0,
    val generalScore: Int = 0
)
