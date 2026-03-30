package com.blue.newsapp.data.remote.model.response

data class CommentResponse(

    val id: Long,

    val userId: Long,

    val newsUrl: String,

    val content: String,

    val createTime: String
)