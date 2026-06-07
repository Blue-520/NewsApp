package com.blue.newsapp.data.loacl.database

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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

    private val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE users ADD COLUMN avatar TEXT NOT NULL DEFAULT ''")
            database.execSQL(
                "ALTER TABLE users ADD COLUMN signature TEXT NOT NULL DEFAULT '这个人很懒，但会认真读每一条新闻'"
            )
        }
    }

    private val MIGRATION_3_4 = object : Migration(3, 4) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE users ADD COLUMN backgroundImage TEXT NOT NULL DEFAULT ''")
        }
    }

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