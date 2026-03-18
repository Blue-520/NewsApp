package com.blue.newsapp.data.loacl.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "news_comment")
data class NewsCommentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val newsUrl: String,                    // 这条评论属于哪一条新闻

    val content: String,                    // 评论内容

    val createTime: Long = System.currentTimeMillis()
)