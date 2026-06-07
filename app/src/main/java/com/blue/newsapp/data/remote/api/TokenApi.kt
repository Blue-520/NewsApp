package com.blue.newsapp.data.remote.api

import com.blue.newsapp.data.remote.model.request.RefreshTokenRequest
import com.blue.newsapp.data.remote.model.response.ApiResponse
import com.blue.newsapp.data.remote.model.response.RefreshTokenResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface TokenApi {

    @POST("/api/auth/refresh")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): ApiResponse<RefreshTokenResponse>
}