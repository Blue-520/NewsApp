package com.blue.newsapp.data.remote.model.request

data class AddFavoriteRequest(

    val url: String,

    val title: String,

    val imageUrl: String?,

    val sourceName: String?,

    val publishedAt: String?,

    val description: String?
)
