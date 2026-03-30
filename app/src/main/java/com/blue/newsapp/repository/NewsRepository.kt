package com.blue.newsapp.repository

import com.blue.newsapp.data.model.Article
import com.blue.newsapp.data.remote.api.NewsApi
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class NewsRepository @Inject constructor(private val newsApi: NewsApi){

    // 这里先简单返回新闻列表
    suspend fun getTopHeadlines(apiKey: String, page: Int, pageSize: Int = 5, requireCategory: String? = null): Result<List<Article>>{

        return try {
            val response = newsApi.getNews(
                country = "us",
                page = page,
                pageSize = pageSize,
                apiKey = apiKey,
                category = requireCategory
            )

            if (response.status == "ok"){
                Result.success(response.articles)
            }else{
                Result.failure(RuntimeException("获取新闻失败：${response.status}"))
            }
        }catch (e: Exception){
            Result.failure(e)
        }
    }

    //搜索新闻
    suspend fun getSearch(query: String): Result<List<Article>>{
        return try {
            val response = newsApi.searchNews(q = query)
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