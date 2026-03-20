package com.blue.newsapp.data.loacl.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.blue.newsapp.data.loacl.dao.FavoriteNewsDao
import com.blue.newsapp.data.loacl.dao.NewsCommentDao
import com.blue.newsapp.data.loacl.dao.NewsDao
import com.blue.newsapp.data.loacl.dao.UserDao
import com.blue.newsapp.data.loacl.entity.FavoriteNewsEntity
import com.blue.newsapp.data.loacl.entity.NewsCommentEntity
import com.blue.newsapp.data.loacl.entity.NewsEntity
import com.blue.newsapp.data.loacl.entity.UserEntity
import kotlin.concurrent.Volatile

@Database(entities = [NewsCommentEntity::class, FavoriteNewsEntity::class, UserEntity::class, NewsEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun favoriteNewsDao() : FavoriteNewsDao
    abstract fun newsCommentDao(): NewsCommentDao
    abstract fun userDao(): UserDao
    abstract fun newsDao(): NewsDao


    companion object{
        @Volatile
        private var instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase{
            return instance ?: synchronized(this){
                instance ?: Room.databaseBuilder(
                    context.applicationContext,                                              //Room.databaseBuilder() 需要一个 Context 参数来访问应用的文件系统，确定数据库文件的存储位置。
                    AppDatabase::class.java,                                          //Application Context（应用级别）,防止内存泄漏
                    "news_app_database"
                )
                    .fallbackToDestructiveMigration()
                    .build().also { instance = it }
            }
        }
    }
}