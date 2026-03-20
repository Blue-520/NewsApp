package com.blue.newsapp.ViewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.blue.newsapp.data.loacl.database.UserPreferences
import com.blue.newsapp.data.loacl.entity.NewsEntity
import com.blue.newsapp.repository.NewLocalRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class RecommendNewsViewModel(application: Application) : AndroidViewModel(application){

    private val repository = NewLocalRepository.getInstance(application)
    private val userPreferences = UserPreferences(application)

    private val currentUserId = MutableLiveData<Long>()

    //新闻列表
    private val _newsList = MutableLiveData<List<NewsEntity>>()
    val newsList : LiveData<List<NewsEntity>> = _newsList

    //加载状态
    private val _loading = MutableLiveData<Boolean>()
    val loading : LiveData<Boolean> = _loading

    //错误状态
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage : LiveData<String?> = _errorMessage

    init {
        observeCurrentUser()
    }

    /**
     * 观察当前登录用户
     */
    private fun observeCurrentUser(){
        viewModelScope.launch {
            userPreferences.userIdFlow.collect { userId ->
                currentUserId.postValue(userId)
                loadRecommendNews(userId)
            }
        }
    }

    /**
     * 加载推荐新闻
     */
    fun loadRecommendNews(userId: Long = currentUserId.value ?: -1L){
        viewModelScope.launch {

            _loading.value = true
            _errorMessage.value = null

            try {
                val news = repository.getRecommendNewsByRatio(userId)
                _newsList.value = news
            }catch (e: Exception){
                e.printStackTrace()
                _errorMessage.value = "新闻加载失败:${e.message}"
                Log.d("aaa", e.message!!)
            }finally {
                _loading.value = false
            }
        }
    }

    //加分
    fun increaseScore(category: String){
        viewModelScope.launch {
            currentUserId.value = userPreferences.userIdFlow.first()
            repository.increaseUserInterestScore(currentUserId.value ?: -1, category, 1)
        }
    }
}