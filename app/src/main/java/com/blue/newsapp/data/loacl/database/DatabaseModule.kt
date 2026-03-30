package com.blue.newsapp.data.loacl.database

import android.content.Context
import androidx.room.Room
import com.blue.newsapp.data.loacl.dao.FavoriteNewsDao
import com.blue.newsapp.data.loacl.dao.NewsCommentDao
import com.blue.newsapp.data.loacl.dao.NewsDao
import com.blue.newsapp.data.loacl.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase{
        return Room.databaseBuilder(context, AppDatabase::class.java, "news_app_database")
            .build()
    }

    @Provides
    fun provideFavoriteNewsDao(database: AppDatabase): FavoriteNewsDao {
        return database.favoriteNewsDao()
    }

    @Provides
    fun provideNewsCommentDao(database: AppDatabase): NewsCommentDao{
        return database.newsCommentDao()
    }

    @Provides
    fun provideUserDao(database: AppDatabase): UserDao{
        return database.userDao()
    }

    @Provides
    fun provideNewsDao(database: AppDatabase): NewsDao{
        return database.newsDao()
    }
}