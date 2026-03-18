package com.blue.newsapp.repository

import com.blue.newsapp.data.model.Article
import com.blue.newsapp.data.network.NetworkModule

object NewsRepository{

    // 这里先简单返回新闻列表
    suspend fun getTopHeadlines(apiKey: String): List<Article>{
        val response = NetworkModule.newsService.getNews(
            "us",
            20,
            apiKey = apiKey
        )

        // 如果接口成功，直接返回 articles
        return response.articles
    }

    //搜索新闻
    suspend fun getSearch(query: String): Result<List<Article>>{
        return try {
            val response = NetworkModule.newsService.searchNews(q = query)
            if (response.status == "ok"){
                Result.success(response.articles)
            }else{
                Result.failure(RuntimeException("搜索失败：${response.status}"))
            }
        }catch (e: Exception){
            Result.failure(e)
        }
    }
}