package com.blue.newsapp.data.remote.model.response

data class UserInfoResponse(

    val userId: Long,

    val username: String,

    val avatar: String,

    val backgroundImage: String,

    val signature: String,

    val businessScore: String,

    val entertainmentScore: Int,

    val healthScore: Int,

    val scienceScore: Int,

    val sportsScore: Int,

    val technologyScore: Int,

    val generalScore: Int
)
