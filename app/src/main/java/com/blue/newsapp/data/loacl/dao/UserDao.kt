package com.blue.newsapp.data.loacl.dao

import androidx.lifecycle.LiveData
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
    suspend fun getUserByUsername(username: String): UserEntity?

    /**
     * 登录时查询用户名+密码是否匹配
     */
    @Query("select * from users where username = :username and password = :password limit 1")
    suspend fun login(username: String, password: String): UserEntity?

    /**
     * 根据用户 id 查询用户
     */
    @Query("select * from users where id = :userId limit 1")
    suspend fun getUserById(userId: Long): UserEntity?

    @Query("select * from users where id = :userId limit 1")
    fun observeUserById(userId: Long): LiveData<UserEntity?>

    /**
     * business 兴趣分数
     */
    @Query("update users set businessScore = businessScore + :delta where id = :userId")
    suspend fun increaseBusinessScore(userId: Long, delta: Int)

    /**
     * entertainment 兴趣分数
     */
    @Query("update users set entertainmentScore = entertainmentScore + :delta where id = :userId")
    suspend fun increaseEntertainmentScore(userId: Long, delta: Int)

    /**
     * health 兴趣分数
     */
    @Query("update users set healthScore = healthScore + :delta where id = :userId")
    suspend fun increaseHealthScore(userId: Long, delta: Int)

    /**
     * science 兴趣分数
     */
    @Query("update users set scienceScore = scienceScore + :delta where id = :userId")
    suspend fun increaseScienceScore(userId: Long, delta: Int)

    /**
     * sports 兴趣分数
     */
    @Query("update users set sportsScore = sportsScore + :delta where id = :userId")
    suspend fun increaseSportsScore(userId: Long, delta: Int)

    /**
     * technology 兴趣分数
     */
    @Query("update users set technologyScore = technologyScore + :delta where id = :userId")
    suspend fun increaseTechnologyScore(userId: Long, delta: Int)

    /**
     * general 兴趣分数
     */
    @Query("update users set generalScore = generalScore + :delta where id = :userId")
    suspend fun increaseGeneralScore(userId: Long, delta: Int)

    @Query("update users set username = :username where id = :userId")
    suspend fun updateUsername(userId: Long, username: String)

    @Query("update users set signature = :signature where id = :userId")
    suspend fun updateSignature(userId: Long, signature: String)

    @Query("update users set avatar = :avatar where id = :userId")
    suspend fun updateAvatar(userId: Long, avatar: String)

    @Query("update users set backgroundImage = :backgroundImage where id = :userId")
    suspend fun updateBackgroundImage(userId: Long, backgroundImage: String)
}
