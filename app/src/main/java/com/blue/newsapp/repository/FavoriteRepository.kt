package com.blue.newsapp.repository

import com.blue.newsapp.data.remote.model.request.AddFavoriteRequest
import com.blue.newsapp.data.remote.model.response.FavoriteNewsResponse
import com.blue.newsapp.data.remote.api.BackendApi
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class FavoriteRepository @Inject constructor(private val backendApi: BackendApi) {

    //从服务器获取收藏新闻
    suspend fun getFavorites(): Result<List<FavoriteNewsResponse>>{
        return try {
            val response = backendApi.getFavorites()
            if (response.code == 200){
                Result.success(response.data ?: emptyList())
            }else{
                Result.failure(Exception(response.message))
            }
        }catch (e: Exception){
            Result.failure(e)
        }
    }

    //向服务器添加收藏的新闻
    suspend fun addFavorite(title: String, imageUrl: String?, sourceName: String?, publishedAt: String?, description: String?, url: String): Result<Unit>{
        return try {
            val response = backendApi.addFavorite(AddFavoriteRequest(url, title, imageUrl, sourceName, publishedAt, description))
            if (response.code == 200) Result.success(Unit)
            else Result.failure(Exception(response.message))
        }catch (e: Exception){
            Result.failure(e)
        }
    }

    //从服务器删除收藏的新闻
    suspend fun removeFavorite(url: String): Result<Unit>{
        return try {
            val response = backendApi.deleteFavorite(url)
            if (response.code == 200) Result.success(Unit)
            else Result.failure(Exception(response.message))
        }catch (e: Exception){
            Result.failure(e)
        }
    }

    //检查新闻收藏状态
    suspend fun isFavorite(url: String): Result<Boolean>{
        return try {
            val response = backendApi.queryFavoriteNews(url)

            if (response.code == 200){
                Result.success(response.data?.favorited == true)
            }else{
                Result.failure(Exception(response.message))
            }
        }catch (e: Exception){
            Result.failure(e)
        }
    }
}