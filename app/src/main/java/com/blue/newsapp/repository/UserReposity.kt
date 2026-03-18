package com.blue.newsapp.repository

import com.blue.newsapp.data.loacl.dao.UserDao
import com.blue.newsapp.data.loacl.entity.UserEntity

class UserReposity(private val userDao: UserDao) {

    /**
     * 注册
     * 返回 Pair<Boolean, String>
     * first = 是否成功
     * second = 提示信息
     */
    suspend fun register(username: String, password: String): Pair<Boolean, String>{
        val existUser = userDao.getUserByUsername(username)
        if (existUser != null){
            return Pair(false, "用户已存在")
        }

        return try {
            userDao.insertUser(UserEntity(username =username, password = password))
            Pair(true, "注册成功")
        }catch (e: Exception){
            Pair(false, "注册失败，请稍后重试")
        }
    }

    /**
     * 登录
     * 这一步先写好，下一步直接接
     */
    suspend fun login(username: String, password: String): UserEntity?{
        return userDao.login(username, password)
    }
}