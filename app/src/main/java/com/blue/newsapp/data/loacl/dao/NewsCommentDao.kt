package com.blue.newsapp.data.loacl.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.blue.newsapp.data.loacl.entity.NewsCommentEntity

@Dao
interface NewsCommentDao {

    @Insert
    suspend fun insertComment(comment: NewsCommentEntity)

    @Query("select * from news_comment where newsUrl = :newsUrl order by createTime desc")
    fun getCommentsNewsUrl(newsUrl: String) : LiveData<List<NewsCommentEntity>>

    @Query("delete from news_comment where id = :commentId")
    suspend fun deleteCommentById(commentId: Int)
}