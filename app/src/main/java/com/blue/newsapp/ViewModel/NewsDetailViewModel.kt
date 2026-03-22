package com.blue.newsapp.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.blue.newsapp.data.loacl.database.UserPreferences
import com.blue.newsapp.data.loacl.entity.FavoriteNewsEntity
import com.blue.newsapp.data.loacl.entity.NewsCommentEntity
import com.blue.newsapp.repository.NewLocalRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class NewsDetailViewModel(application: Application): AndroidViewModel(application) {

    private val repository = NewLocalRepository.getInstance(application)
    private val userPreFerences = UserPreferences(application)

    // 当前详情页对应的新闻 url
    private val currentNewsUrl = MutableLiveData<String>()

    //当前登录用户id
    private val currentUserId = MutableLiveData<Long>()

    // 当前新闻的收藏状态（和当前登录用户绑定）
    private val _favoriteNews = MediatorLiveData<FavoriteNewsEntity?>()
    val favoriteNews: LiveData<FavoriteNewsEntity?> = _favoriteNews

    private var favoriteSource: LiveData<FavoriteNewsEntity?>? = null

    init {
        viewModelScope.launch {
            userPreFerences.userIdFlow.collect { userId ->
                currentUserId.value = userId
            }
        }

        _favoriteNews.addSource(currentUserId) { userId ->
            refreshFavoriteSource()
        }

        _favoriteNews.addSource(currentNewsUrl) { url ->
            refreshFavoriteSource()
        }
    }

    /**
     * 根据当前 userId 和 newsUrl 重新切换收藏数据源
     */
    private fun refreshFavoriteSource() {
        val userId = currentUserId.value
        val newsUrl = currentNewsUrl.value

        // 先移除旧数据源
        favoriteSource?.let {
            _favoriteNews.removeSource(it)
            favoriteSource = null
        }

        // 未登录 or url 为空，直接视为未收藏
        if (userId == null || userId == -1L || newsUrl.isNullOrEmpty()) {
            _favoriteNews.value = null
            return
        }

        // 切换到新的查询结果
        favoriteSource = repository.getFavoriteNewsByUserIdAndUrl(userId, newsUrl)
        favoriteSource?.let { source ->
            _favoriteNews.addSource(source) { favorite ->
                _favoriteNews.value = favorite
            }
        }
    }

    //给兴趣加分
    fun increaseScore(category: String){
        viewModelScope.launch {
            val userId = userPreFerences.userIdFlow.first()

            if (userId == -1L) return@launch

            repository.increaseUserInterestScore(userId, category, 2)
        }
    }



    // 当前新闻的评论列表
    val commentList: LiveData<List<NewsCommentEntity>> = currentNewsUrl.switchMap { url ->
        repository.getCommentsByNewsUrl(url)
    }

    /**
     * 设置当前新闻 url
     * 详情页打开时调用一次
     */
    fun setNewsUrl(url: String){
        if (currentNewsUrl.value != url){
            currentNewsUrl.value = url
        }
    }


    /**
     * 添加收藏
     */
    fun addFavorite(title: String, imageUrl: String, sourceName: String, publishedAt: String, description: String, url: String){
        viewModelScope.launch {
            val userId = userPreFerences.userIdFlow.first()

            if (userId == -1L) return@launch

            repository.insertFavorite(FavoriteNewsEntity(userId = userId, url = url, title = title, imageUrl = imageUrl, sourceName = sourceName, publishedAt = publishedAt, description = description))
        }
    }

    /**
     * 取消收藏
     */
    fun removeFavorite(url: String){
        viewModelScope.launch {
            val userId = userPreFerences.userIdFlow.first()

            if (userId == -1L) return@launch

            repository.deleteFavoriteByUserIdAndUrl(userId, url)
        }
    }

    /**
     * 添加评论
     */
    fun addComment(newsUrl: String, content: String){
        viewModelScope.launch {
            repository.insertComment(NewsCommentEntity(newsUrl = newsUrl, content = content))
        }
    }

    /**
     * 判断当前是否已登录
     */
    suspend fun isLogin(): Boolean{
        return userPreFerences.isLoginFlow.first()
    }
}