package com.blue.newsapp.data.remote.api

import com.blue.newsapp.data.remote.model.request.AddCommentRequset
import com.blue.newsapp.data.remote.model.request.AddFavoriteRequest
import com.blue.newsapp.data.remote.model.request.LoginRequest
import com.blue.newsapp.data.remote.model.request.RefreshTokenRequest
import com.blue.newsapp.data.remote.model.request.RegisterRequest
import com.blue.newsapp.data.remote.model.response.CommentResponse
import com.blue.newsapp.data.remote.model.response.ApiResponse
import com.blue.newsapp.data.remote.model.response.FavoriteNewsResponse
import com.blue.newsapp.data.remote.model.response.LoginResponse
import com.blue.newsapp.data.remote.model.response.QueryFavoriteNewsResponse
import com.blue.newsapp.data.remote.model.response.RefreshTokenResponse
import com.blue.newsapp.data.remote.model.response.UserInfoResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface BackendApi {

    @POST("/api/auth/register")
    suspend fun register(@Body request: RegisterRequest): ApiResponse<Unit>

    @POST("/api/auth/login")
    suspend fun login(@Body request: LoginRequest): ApiResponse<LoginResponse>

    @GET("/api/user/me")
    suspend fun getCurrentUser(): ApiResponse<UserInfoResponse>

    @GET("/api/favorites/status")
    suspend fun queryFavoriteNews(@Query("url") url: String): ApiResponse<QueryFavoriteNewsResponse>

    @GET("/api/favorites")
    suspend fun getFavorites(): ApiResponse<List<FavoriteNewsResponse>>

    @POST("/api/favorites")
    suspend fun addFavorite(@Body request: AddFavoriteRequest): ApiResponse<Unit>

    @DELETE("/api/favorites")
    suspend fun deleteFavorite(@Query("url") url: String): ApiResponse<Unit>

    @POST("/api/comments")
    suspend fun addComment(@Body requset: AddCommentRequset): ApiResponse<CommentResponse>

    @GET("/api/comments")
    suspend fun checkComment(@Query("newsUrl") url: String): ApiResponse<List<CommentResponse>>
}
