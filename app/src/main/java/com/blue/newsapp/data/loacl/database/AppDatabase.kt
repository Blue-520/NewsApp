package com.blue.newsapp.data.loacl.database

import android.content.Context
import androidx.room.migration.Migration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.blue.newsapp.data.loacl.dao.FavoriteNewsDao
import com.blue.newsapp.data.loacl.dao.NewsCommentDao
import com.blue.newsapp.data.loacl.dao.NewsDao
import com.blue.newsapp.data.loacl.dao.UserDao
import com.blue.newsapp.data.loacl.entity.FavoriteNewsEntity
import com.blue.newsapp.data.loacl.entity.NewsCommentEntity
import com.blue.newsapp.data.loacl.entity.NewsEntity
import com.blue.newsapp.data.loacl.entity.UserEntity
import kotlin.concurrent.Volatile

@Database(entities = [NewsCommentEntity::class, FavoriteNewsEntity::class, UserEntity::class, NewsEntity::class], version = 4, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun favoriteNewsDao() : FavoriteNewsDao
    abstract fun newsCommentDao(): NewsCommentDao
    abstract fun userDao(): UserDao
    abstract fun newsDao(): NewsDao


    companion object{
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

        @Volatile
        private var instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase{
            return instance ?: synchronized(this){
                instance ?: Room.databaseBuilder(
                    context.applicationContext,                                              //Room.databaseBuilder() 需要一个 Context 参数来访问应用的文件系统，确定数据库文件的存储位置。
                    AppDatabase::class.java,                                          //Application Context（应用级别）,防止内存泄漏
                    "news_app_database"
                )
                    .addMigrations(MIGRATION_2_3)
                    .addMigrations(MIGRATION_3_4)
                    .build().also { instance = it }
            }
        }
    }
}
