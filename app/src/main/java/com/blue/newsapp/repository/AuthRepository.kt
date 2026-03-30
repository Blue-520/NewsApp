package com.blue.newsapp.repository

import com.blue.newsapp.data.remote.model.request.LoginRequest
import com.blue.newsapp.data.remote.model.request.RegisterRequest
import com.blue.newsapp.data.loacl.database.SessionManager
import com.blue.newsapp.data.remote.api.BackendApi
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class AuthRepository @Inject constructor(private val backendApi: BackendApi, private val sessionManager: SessionManager) {

    suspend fun login(username: String, password: String): Result<Unit>{
        return try {
            val response = backendApi.login(LoginRequest(username, password))
            val data = response.data
            if (response.code == 200 && data != null){
                sessionManager.saveLoginSession(
                    username = data.username,
                    userId = data.userId,
                    accessToken = data.accessToken,
                    refreshToken = data.refreshToken
                )
                Result.success(Unit)
            }else{
                Result.failure(Exception(response.message))
            }
        }catch (e: Exception){
            Result.failure(e)
        }
    }

    suspend fun register(username: String, password: String): Result<Unit>{
        return try {
            val response = backendApi.register(RegisterRequest(username, password))
            if (response.code == 200){
                Result.success(Unit)
            }else{
                Result.failure(Exception(response.message))
            }
        }catch (e: Exception){
            Result.failure(e)
        }
    }

    suspend fun logout(){
        sessionManager.clearSession()
    }

    fun observeSession(): Flow<SessionManager.UserSession> = sessionManager.sessionFlow
}