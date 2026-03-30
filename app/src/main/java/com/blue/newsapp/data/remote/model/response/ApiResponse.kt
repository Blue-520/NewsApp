package com.blue.newsapp.data.remote.model.response

data class ApiResponse<T>(

    val code: Int,                      //状态码

    val message: String,                //提示信息

    val data: T?                        //实际数据
)
