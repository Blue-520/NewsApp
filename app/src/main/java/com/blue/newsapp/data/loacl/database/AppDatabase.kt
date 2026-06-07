package com.blue.newsapp.data.loacl.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.blue.newsapp.data.loacl.dao.FavoriteNewsDao
import com.blue.newsapp.data.loacl.dao.NewsCommentDao
import com.blue.newsapp.data.loacl.dao.NewsDao
import com.blue.newsapp.data.loacl.dao.UserDao
import com.blue.newsapp.data.loacl.entity.FavoriteNewsEntity
import com.blue.newsapp.data.loacl.entity.NewsCommentEntity
import com.blue.newsapp.data.loacl.entity.NewsEntity
import com.blue.newsapp.data.loacl.entity.UserEntity

@Database(entities = [NewsCommentEntity::class, FavoriteNewsEntity::class, UserEntity::class, NewsEntity::class], version = 4, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun favoriteNewsDao() : FavoriteNewsDao
    abstract fun newsCommentDao(): NewsCommentDao
    abstract fun userDao(): UserDao
    abstract fun newsDao(): NewsDao
}