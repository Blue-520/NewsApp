package com.blue.newsapp.data.loacl.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.blue.newsapp.data.loacl.entity.FavoriteNewsEntity

@Dao
interface FavoriteNewsDao {

    /**
     * 插入收藏
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(news: FavoriteNewsEntity)

    /**
     * 删除某个用户的某篇收藏
     */
    @Query("delete from favorite_news where userId = :userId and url = :url")
    suspend fun deleteFavoriteByUserIdAndUrl(userId: Long, url: String)

    /**
     * 查询某个用户是否已收藏某篇新闻（通过 url 判断）
     */
    @Query("select * from favorite_news where userId = :userId and url = :url limit 1")
    fun getFavoriteNewsUserIdAndUrl(userId: Long, url: String) : LiveData<FavoriteNewsEntity?>

    /**
     * 查询某个用户的全部收藏
     */
    @Query("select * from favorite_news where userId = :userId order by savedTime desc")
    fun getAllFavoriteNewsByUserId(userId: Long) : LiveData<List<FavoriteNewsEntity>>
}