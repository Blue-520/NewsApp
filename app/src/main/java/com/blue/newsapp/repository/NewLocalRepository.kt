package com.blue.newsapp.repository

import android.content.Context
import com.blue.newsapp.data.loacl.dao.FavoriteNewsDao
import com.blue.newsapp.data.loacl.dao.NewsCommentDao
import com.blue.newsapp.data.loacl.dao.NewsDao
import com.blue.newsapp.data.loacl.dao.UserDao
import com.blue.newsapp.data.loacl.entity.FavoriteNewsEntity
import com.blue.newsapp.data.loacl.entity.NewsCommentEntity
import com.blue.newsapp.data.loacl.entity.NewsEntity
import com.blue.newsapp.data.model.Article
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlin.concurrent.Volatile


@Singleton
class NewLocalRepository @Inject constructor(
    private val favoriteNewsDao: FavoriteNewsDao,
    private val newsCommentDao: NewsCommentDao,
    private val newsDao: NewsDao,
    private val userDao: UserDao){

    // ==================== 收藏 ====================

    fun getFavoriteNewsByUserIdAndUrl(userId: Long, url: String) = favoriteNewsDao.getFavoriteNewsUserIdAndUrl(userId, url)

    fun getAllFavoriteNewsByUserId(userId: Long) = favoriteNewsDao.getAllFavoriteNewsByUserId(userId)

    suspend fun insertFavorite(news: FavoriteNewsEntity){
        favoriteNewsDao.insertFavorite(news)
    }

    suspend fun deleteFavoriteByUserIdAndUrl(userId: Long, url: String){
        favoriteNewsDao.deleteFavoriteByUserIdAndUrl(userId, url)
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
            "business" -> userDao.increaseBusinessScore(userId, delta)
            "entertainment" -> userDao.increaseEntertainmentScore(userId, delta)
            "health" -> userDao.increaseHealthScore(userId, delta)
            "science" -> userDao.increaseScienceScore(userId, delta)
            "sports" -> userDao.increaseSportsScore(userId, delta)
            "technology" -> userDao.increaseTechnologyScore(userId, delta)
            "general" -> userDao.increaseGeneralScore(userId, delta)
        }
    }

    //根据兴趣推荐新闻
    suspend fun getRecommendNewsByRatio(userId: Long): List<NewsEntity>{
        // 目标返回 20 条新闻
        val targetCount = 20

        // 1. 查询用户
        val user = userDao.getUserById(userId) ?: return newsDao.getLatestNews(20)

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
}