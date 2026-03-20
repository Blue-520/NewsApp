package com.blue.newsapp.data.loacl.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.blue.newsapp.data.loacl.entity.NewsEntity

@Dao
interface NewsDao {

    /**
     * 插入新闻列表
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNewsList(newsList: List<NewsEntity>)

    /**
     * 插入新闻列表
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNews(news: NewsEntity)

    /**
     * 根据发布时间查询20条数据
     */
    @Query("select * from localNews order by publishedAt desc limit :limit")
    suspend fun getLatestNews(limit: Int): List<NewsEntity>

    /**
     * 根据兴趣查询数据
     */
    @Query("select * from localNews where category = :category order by RANDOM() desc limit :limit")
    suspend fun getNewsByCategoryLimit(category: String, limit: Int): List<NewsEntity>

}