package com.blue.newsapp.data.remote.model.response

data class FavoriteNewsResponse(

    val id: Long,                       //收藏ID

    val userId: Long,

    val url: String,

    val title: String,

    val imageUrl: String,

    val sourceName:String,

    val publishedAt: String,

    val description: String,

    val savedTime: Long
)
