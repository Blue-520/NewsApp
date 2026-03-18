package com.blue.newsapp.data.loacl.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_news")
data class FavoriteNewsEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,                                      // 自增主键

    val userId: Long,                                       // 这个收藏属于哪个用户

    val url: String,                                        // 新闻原文链接作

    val title: String,

    val imageUrl: String,

    val sourceName: String,

    val publishedAt: String,

    val description: String,

    val savedTime: Long = System.currentTimeMillis()           //收藏时间
)