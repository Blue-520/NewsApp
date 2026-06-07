package com.blue.newsapp.ui

sealed class NewsDetailEvent {
    data class ShowToast(val message: String): NewsDetailEvent()

    data object ClearCommentInput: NewsDetailEvent()

    data object NavigateToLogin: NewsDetailEvent()
}