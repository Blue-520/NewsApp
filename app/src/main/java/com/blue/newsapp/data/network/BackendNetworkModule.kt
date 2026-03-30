package com.blue.newsapp.data.network

import com.blue.newsapp.data.loacl.database.SessionManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import com.blue.newsapp.BuildConfig
import com.blue.newsapp.data.remote.api.BackendApi
import com.blue.newsapp.data.remote.api.TokenApi
import com.blue.newsapp.data.remote.interceptor.AuthInterceptor
import com.blue.newsapp.data.remote.interceptor.TokenAuthenticator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Qualifier
import jakarta.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class TokenOkHttpClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MainOkHttpClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BackendLoggingInterceptor


@Module
@InstallIn(SingletonComponent::class)
object BackendNetworkModule {

    private const val BASE_URL = "http://10.0.2.2"

    //日志拦截器
    @Provides
    @Singleton
    @BackendLoggingInterceptor
    fun provideLoggingInterceptor(): HttpLoggingInterceptor{
        return HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG){
                HttpLoggingInterceptor.Level.BASIC
            }else{
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }

    //请求刷新TokenClient
    @Provides
    @Singleton
    @TokenOkHttpClient
    fun provideTokenOkHttpClient(@BackendLoggingInterceptor loggingInterceptor: HttpLoggingInterceptor): OkHttpClient{
        return OkHttpClient.Builder()
            .connectTimeout(3, TimeUnit.SECONDS)
            .readTimeout(3, TimeUnit.SECONDS)
            .writeTimeout(3, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    //后端Client
    @Provides
    @Singleton
    @MainOkHttpClient
    fun provideMainOkHttpClient(
        authInterceptor: AuthInterceptor,
        tokenAuthenticator: TokenAuthenticator,
        @BackendLoggingInterceptor loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient{
        return OkHttpClient.Builder()
            .connectTimeout(3, TimeUnit.SECONDS)
            .readTimeout(3, TimeUnit.SECONDS)
            .writeTimeout(3, TimeUnit.SECONDS)
            .addInterceptor(authInterceptor)
            .authenticator(tokenAuthenticator)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    //请求刷新Token的Retrofit
    @Provides
    @Singleton
    fun provideTokenApi(@TokenOkHttpClient tokenClient: OkHttpClient): TokenApi{
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(tokenClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TokenApi::class.java)
    }

    //后端Retrofit
    @Provides
    @Singleton
    fun provideBackApi(@MainOkHttpClient mainOkHttpClient: OkHttpClient): BackendApi{
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(mainOkHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BackendApi::class.java)
    }
}
