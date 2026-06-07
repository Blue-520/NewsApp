package com.blue.newsapp.repository

import com.blue.newsapp.data.remote.api.BackendApi
import com.blue.newsapp.data.remote.model.request.AddCommentRequset
import com.blue.newsapp.data.remote.model.response.CommentResponse
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class CommentReposity @Inject constructor(private val backendApi: BackendApi) {

    suspend fun checkComment(url: String): Result<List<CommentResponse>>{
        return try {
            val response = backendApi.checkComment(url)

            if (response.code == 200 && response.data != null){
                Result.success(response.data)
            }else{
                Result.failure(Exception(response.message))
            }
        }catch (e: Exception){
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun addComment(commentRequset: AddCommentRequset): Result<CommentResponse>{
        return try {
            val response = backendApi.addComment(commentRequset)

            if (response.code == 200 && response.data != null){
                Result.success(response.data)
            }else{
                Result.failure(Exception(response.message))
            }
        }catch (e: Exception){
            e.printStackTrace()
            Result.failure(e)
        }
    }
}