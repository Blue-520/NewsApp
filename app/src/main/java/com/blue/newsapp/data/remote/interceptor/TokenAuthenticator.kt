package com.blue.newsapp.data.remote.interceptor

import com.blue.newsapp.data.remote.model.request.RefreshTokenRequest
import com.blue.newsapp.data.loacl.database.SessionManager
import com.blue.newsapp.data.remote.api.TokenApi
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

@Singleton
class TokenAuthenticator @Inject constructor(private val sessionManager: SessionManager, private val tokenApi: TokenApi) : Authenticator {

    // 用来保证同一时间只有一个线程执行刷新 token 逻辑
    private val lock = Any()

    override fun authenticate(route: Route?, response: Response): Request? {
        // 如果已经重试过太多次，就不要再试了，防止死循环
        if (responseCount(response) >= 2) return null

        synchronized(lock) {
            // 读取当前本地最新的 accessToken
            val currentToken = runBlocking { sessionManager.getAccessToken() }

            // 读取这次失败请求里携带的 token
            val requestToken = response.request.header("Authorization")
                ?.removePrefix("Bearer ")
                ?.trim()

            // 如果本地 token 已经和失败请求里的 token 不一样，
            // 说明别的请求已经刷新成功了，这里直接用本地新 token 重试即可
            if (!currentToken.isNullOrBlank() && currentToken != requestToken) {
                return response.request.newBuilder()
                    .header("Authorization", "Bearer $currentToken")
                    .build()
            }

            // 拿 refreshToken 准备去刷新
            val refreshToken = runBlocking { sessionManager.getRefreshToken() }

            // 如果连 refreshToken 都没有，说明登录态无效了
            if (refreshToken.isBlank()) {
                runBlocking { sessionManager.clearSession() }
                return null
            }

            return try {
                // 调用刷新接口
                val refreshResponse = runBlocking { tokenApi.refreshToken(RefreshTokenRequest(refreshToken)) }

                // 刷新成功
                if (refreshResponse.code == 200 && refreshResponse.data != null) {
                    val newAccessToken = refreshResponse.data.accessToken
                    val newRefreshToken = refreshResponse.data.refreshToken

                    // 保存新的 token
                    runBlocking {
                        sessionManager.updateTokens(
                            accessToken = newAccessToken,
                            refreshToken = newRefreshToken
                        )
                    }

                    // 用新 accessToken 重建原请求并返回，让 OkHttp 自动重试
                    response.request.newBuilder()
                        .header("Authorization", "Bearer $newAccessToken")
                        .build()
                } else {
                    // 刷新失败，清空本地登录态
                    runBlocking { sessionManager.clearSession() }
                    null
                }
            } catch (e: Exception) {
                // 刷新过程异常，也视为失败
                runBlocking { sessionManager.clearSession() }
                null
            }
        }
    }

    private fun responseCount(response: Response): Int {
        var result = 1
        var prior = response.priorResponse
        while (prior != null) {
            result++
            prior = prior.priorResponse
        }
        return result
    }
}