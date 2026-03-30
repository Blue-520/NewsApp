package com.blue.newsapp.ui

sealed class UiState<out T> {
    data object Loading: UiState<Nothing>()

    data class Empty(val message: String = "暂无数据"): UiState<Nothing>()

    data class Success<T>(val data: T): UiState<T>()

    data class Error(val message: String): UiState<Nothing>()
}