package com.blue.newsapp.data.loacl.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.blue.newsapp.data.loacl.entity.UserEntity

@Dao
interface UserDao{

    /**
     * 插入新用户
     * 返回插入后的行 id
     */
    @Insert
    suspend fun insertUser(user: UserEntity): Long

    /**
     * 根据用户名查询用户
     */
    @Query("select * from users where username = :username limit 1")
    suspend fun getUserByUsername(username: String) : UserEntity?

    /**
     * 登录时查询用户名+密码是否匹配
     */
    @Query("select * from users where username = :username and password = :password")
    suspend fun login(username: String, password: String) : UserEntity?
}