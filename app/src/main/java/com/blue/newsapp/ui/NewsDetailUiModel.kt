package com.blue.newsapp.ui

import com.blue.newsapp.data.remote.model.response.CommentResponse

data class NewsDetailUiModel(

    val newsUrl: String,

    val isFavorite: Boolean = false,

    val comments: List<CommentResponse> = emptyList(),

    val favoriteLoading: Boolean = false,

    val commentSubmitting: Boolean = false
)