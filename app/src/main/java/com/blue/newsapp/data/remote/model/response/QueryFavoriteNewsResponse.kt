package com.blue.newsapp.data.remote.model.response

import com.blue.newsapp.repository.FavoriteRepository

data class QueryFavoriteNewsResponse(

    val favorited: Boolean,

    val favorite: FavoriteRepository
)
