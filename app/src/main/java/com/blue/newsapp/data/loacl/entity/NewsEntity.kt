package com.blue.newsapp.data.loacl.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "localNews")
data class NewsEntity(
    @PrimaryKey
    val url: String,              // 新闻网页链接

    val id: String?,              // 来源 id，可能为空，所以用可空类型

    val name: String,             // 来源名称

    val author: String?,          // 作者，可能为空

    val title: String,            // 标题

    val description: String?,     // 简介，可能为空

    val urlToImage: String?,      // 新闻图片链接，可能为空

    val publishedAt: String?,     // 发布时间，可能为空

    val content: String?,         // 正文摘要，可能为空

    val category: String?         //类型
)