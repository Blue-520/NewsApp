package com.blue.newsapp.data.remote.model.response

data class LoginResponse(

    val userId: Long,

    val username: String,

    val accessToken: String,

    val refreshToken: String,

    val avatar: String,                             //头像地址

    val signature: String                           //个性签名
)
