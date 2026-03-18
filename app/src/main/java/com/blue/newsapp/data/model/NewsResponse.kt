package com.blue.newsapp.data.model

data class NewsResponse(
    val status: String,                   //响应状态

    val totalResults: Int,               // 总结果数

    val articles: List<Article>             //新闻列表
)

