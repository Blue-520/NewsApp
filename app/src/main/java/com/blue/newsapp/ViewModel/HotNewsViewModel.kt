package com.blue.newsapp.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.blue.newsapp.data.loacl.database.UserPreferences
import com.blue.newsapp.data.model.Article
import com.blue.newsapp.data.network.NewsConstants
import com.blue.newsapp.repository.NewLocalRepository
import com.blue.newsapp.repository.NewsRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class HotNewsViewModel(application: Application) : AndroidViewModel(application){

    val repository = NewLocalRepository.getInstance(application)

    private val _newsList = MutableLiveData<List<Article>>()
    val newList: LiveData<List<Article>> = _newsList

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private var hasLoaded = false

    fun loadNewsIfNeeded(){
        if (hasLoaded && !_newsList.value.isNullOrEmpty()){
            return
        }else{
            loadNews()
        }
    }

    fun loadNews(){
        viewModelScope.launch {

            _loading.value = true
            _errorMessage.value = null

            try {
                val requestCategory: String? = "health"
                // 1. 请求一次接口
                val articleList = NewsRepository.getTopHeadlines(NewsConstants.apiKey, requestCategory)

                // 2. 保存到本地数据库
                repository.saveNewsList(articleList, requestCategory)

                // 3. 显示到页面
                _newsList.value = articleList

                hasLoaded = true

            }catch (e: Exception){
                e.printStackTrace()
                _errorMessage.value = "新闻加载失败：${e.message}"
            }finally {
                _loading.value = false
            }

        }
    }
}