package com.blue.newsapp.data.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkModule {

    // 这里先把 baseUrl 单独提出来，后面如果换接口平台，只改这里
    private const val BASE_URL = "https://newsapi.org"

    // 日志拦截器：开发阶段很好用，可以在 Logcat 里看请求和响应
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // OkHttpClient：Retrofit 底层就是用它来真正发请求
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)               //连接超时
        .readTimeout(10, TimeUnit.SECONDS)                  //读取超时
        .writeTimeout(10, TimeUnit.SECONDS)                 //写入超市
        .addInterceptor(loggingInterceptor)                                 //添加日志拦截器
        .build()

    private val retrofit =  Retrofit.Builder()
        .baseUrl(BASE_URL)                                          //基础地址
        .client(okHttpClient)                                                //绑定OkHttp
        .addConverterFactory(GsonConverterFactory.create())         //Gson自动解析JSON
        .build()

    //对外暴露接口实例
    val newsService: NewsService = retrofit.create<NewsService>(NewsService::class.java)
}