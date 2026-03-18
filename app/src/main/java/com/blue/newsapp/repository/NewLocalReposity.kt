package com.blue.newsapp.repository

import android.content.Context
import com.blue.newsapp.data.loacl.database.AppDatabase
import com.blue.newsapp.data.loacl.entity.FavoriteNewsEntity
import com.blue.newsapp.data.loacl.entity.NewsCommentEntity
import kotlin.concurrent.Volatile

class NewLocalReposity private constructor(context: Context){

    private val favoriteDao = AppDatabase.getDatabase(context).favoriteNewsDao()
    private val newsCommentDao = AppDatabase.getDatabase(context).newsCommentDao()

    // ==================== 收藏 ====================

    fun getFavoriteNewsByUserIdAndUrl(userId: Long, url: String) = favoriteDao.getFavoriteNewsUserIdAndUrl(userId, url)

    fun getAllFavoriteNewsByUserId(userId: Long) = favoriteDao.getAllFavoriteNewsByUserId(userId)

    suspend fun insertFavorite(news: FavoriteNewsEntity){
        favoriteDao.insertFavorite(news)
    }

    suspend fun deleteFavoriteByUserIdAndUrl(userId: Long, url: String){
        favoriteDao.deleteFavoriteByUserIdAndUrl(userId, url)
    }

    // ==================== 评论 ====================

    fun getCommentsByNewsUrl(newsUrl: String) = newsCommentDao.getCommentsNewsUrl(newsUrl)

    suspend fun insertComment(comment: NewsCommentEntity){
        newsCommentDao.insertComment(comment)
    }

    companion object{
        @Volatile
        private var instance: NewLocalReposity? = null

        fun getInstance(context: Context): NewLocalReposity{
            return instance ?: synchronized(this){
                instance ?: NewLocalReposity(context).also { instance = it }
            }
        }
    }
}