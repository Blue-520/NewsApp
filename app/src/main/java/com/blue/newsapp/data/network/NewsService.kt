package com.blue.newsapp.data.network

import com.blue.newsapp.data.model.NewsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsService{

    @GET("v2/top-headlines")
    suspend fun getNews(

        //获取头条新闻
        @Query("country")               //括号里面是api链接里面的真是字段
        country: String = "us",

        @Query("pageSize")              // 每页条数，先给 20 条
        pageSize: Int = 20,

        @Query("apiKey")
        apiKey: String

    ): NewsResponse


    @GET("everything")
    suspend fun searchNews(

        @Query("q")
        q: String,

        @Query("language")
        language: String = "zh",

        @Query("sortBy")
        sortBy: String = "publishedAt",

        @Query("pageSize")
        pageSize: Int = 20,

        @Query("apiKey")
        apiKey: String = NewsConstants.apiKey
    ): NewsResponse
}