package com.blue.newsapp.data.remote.api

import com.blue.newsapp.data.model.NewsResponse
import com.blue.newsapp.data.network.NewsConstants
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi{

    @GET("v2/top-headlines")
    suspend fun getNews(

        //获取头条新闻
        @Query("country")               //括号里面是api链接里面的真是字段
        country: String = "us",

        @Query("page")
        page: Int = 1,

        @Query("pageSize")              // 每页条数，先给 20 条
        pageSize: Int = 5,

        @Query("apiKey")
        apiKey: String,

        @Query("category")
        category: String?,

    ): NewsResponse


    @GET("v2/everything")
    suspend fun searchNews(

        @Query("q")
        q: String,

        @Query("sortBy")
        sortBy: String = "publishedAt",

        @Query("page")
        page: Int = 1,

        @Query("pageSize")
        pageSize: Int = 20,

        @Query("apiKey")
        apiKey: String
    ): NewsResponse
}