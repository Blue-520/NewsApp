package com.blue.newsapp.repository

import android.content.Context
import com.blue.newsapp.data.loacl.database.AppDatabase
import com.blue.newsapp.data.loacl.entity.FavoriteNewsEntity
import com.blue.newsapp.data.loacl.entity.NewsCommentEntity
import com.blue.newsapp.data.loacl.entity.NewsEntity
import com.blue.newsapp.data.model.Article
import kotlin.concurrent.Volatile

class NewLocalRepository private constructor(context: Context){

    private val favoriteDao = AppDatabase.getDatabase(context).favoriteNewsDao()
    private val newsCommentDao = AppDatabase.getDatabase(context).newsCommentDao()
    private var newsDao = AppDatabase.getDatabase(context).newsDao()
    private val userDoa = AppDatabase.getDatabase(context).userDao()

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

    // ==================== 新闻 ====================

    //API获取新闻列表保存数据库
    suspend fun saveNewsList(articleList: List<Article>, requestCategory: String?){
        val newsList = articleList.map { article ->
            NewsEntity(
                url = article.url,
                id = article.source.id,
                name = article.source.name,
                author = article.author,
                title = article.title,
                description = article.description,
                urlToImage = article.urlToImage,
                publishedAt = article.publishedAt,
                content = article.content,
                category = requestCategory ?: "general"
            )
        }

        newsDao.insertNewsList(newsList)
    }

    //API获取新闻保存数据库
    suspend fun saveNews(article: Article, requestCategory: String?){
        val news = NewsEntity(url = article.url,
            id = article.source.id,
            name = article.source.name,
            author = article.author,
            title = article.title,
            description = article.description,
            urlToImage = article.urlToImage,
            publishedAt = article.publishedAt,
            content = article.content,
            category = requestCategory ?: "general")

        newsDao.insertNews(news)
    }

    //兴趣加分
    suspend fun increaseUserInterestScore(userId: Long, category: String, delta: Int) {
        when (category) {
            "business" -> userDoa.increaseBusinessScore(userId, delta)
            "entertainment" -> userDoa.increaseEntertainmentScore(userId, delta)
            "health" -> userDoa.increaseHealthScore(userId, delta)
            "science" -> userDoa.increaseScienceScore(userId, delta)
            "sports" -> userDoa.increaseSportsScore(userId, delta)
            "technology" -> userDoa.increaseTechnologyScore(userId, delta)
            "general" -> userDoa.increaseGeneralScore(userId, delta)
        }
    }

    //根据兴趣推荐新闻
    suspend fun getRecommendNewsByRatio(userId: Long): List<NewsEntity>{
        // 目标返回 20 条新闻
        val targetCount = 20

        // 1. 查询用户
        val user = userDoa.getUserById(userId) ?: return newsDao.getLatestNews(20)

        // 2. 读取用户兴趣分数
        val categoryScoreMap  = linkedMapOf(
            "business" to user.businessScore,
            "entertainment" to user.entertainmentScore,
            "health" to user.healthScore,
            "science" to user.scienceScore,
            "sports" to user.sportsScore,
            "technology" to user.technologyScore,
            "general" to user.generalScore
        )

        // 3. 计算总分
        val totalScore = categoryScoreMap.values.sum()
        if (totalScore <= 0){
            return newsDao.getLatestNews(targetCount)
        }

        // 4. 计算每个分类应该分到多少条新闻
        val categoryNewsCountMap = mutableMapOf<String, Int>()
        var assignedCount = 0

        for ((category, score) in categoryScoreMap ){
            val newsCount = targetCount * score / totalScore
            categoryNewsCountMap[category] = newsCount
            assignedCount += newsCount
        }

        // 5. 如果因为整数除法还没分满 20 条，就补给高兴趣分类
        var leftCount = targetCount - assignedCount

        val highScoreCategoryList = categoryScoreMap.entries
            .sortedByDescending { it.value }
            .map { it.key }

        var i = 0
        while (leftCount > 0 && highScoreCategoryList.isNotEmpty()){
            val category = highScoreCategoryList[i % highScoreCategoryList.size]
            val oldCount = categoryNewsCountMap[category] ?: 0
            categoryNewsCountMap[category] = oldCount + 1
            leftCount--
            i++
        }

        // 6. 按分类取新闻
        val recommendNewsList = mutableListOf<NewsEntity>()

        for ((category, needCount) in categoryNewsCountMap){
            if (needCount > 0){
                val categoryNewsList = newsDao.getNewsByCategoryLimit(category, needCount)
                recommendNewsList.addAll(categoryNewsList)
            }
        }

        return recommendNewsList.take(targetCount)
    }

    companion object{
        @Volatile
        private var instance: NewLocalRepository? = null

        fun getInstance(context: Context): NewLocalRepository{
            return instance ?: synchronized(this){
                instance ?: NewLocalRepository(context).also { instance = it }
            }
        }
    }
}