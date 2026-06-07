package com.blue.newsapp.data.remote.interceptor

import com.blue.newsapp.data.loacl.database.SessionManager
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

@Singleton
class AuthInterceptor @Inject constructor(private val sessionManager: SessionManager): Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        // 1. 拿到当前原始请求
        val originalRequest = chain.request()

        // 2. 不需要携带 token 的接口
        val noAuthPaths = listOf(
            "/api/auth/login",
            "/api/auth/register",
            "/api/auth/refresh"
        )

        // 3. 取出当前请求路径，例如 /api/auth/login
        val path = originalRequest.url.encodedPath
        // 4. 如果当前请求属于免认证接口，直接放行
        if (noAuthPaths.any { path == it }){
            return chain.proceed(originalRequest)
        }

        // 5. 从本地同步取出 accessToken
        val accessToken = runBlocking {
            sessionManager.getAccessToken()
        }

        // 6. 如果 token 不为空，就给请求头加上 Authorization
        val request = if (accessToken.isNotBlank()){
            originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer $accessToken")
                .build()
        }else{
            // 没有 token，就还是用原请求
            originalRequest
        }

        // 7. 继续发送请求
        return chain.proceed(request)
    }
}