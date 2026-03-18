package com.blue.newsapp.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blue.newsapp.data.model.Article
import com.blue.newsapp.data.network.NewsConstants
import com.blue.newsapp.repository.NewsRepository
import kotlinx.coroutines.launch

class NewsViewModel: ViewModel() {

    // 对外暴露的新闻列表
    private val _newsList = MutableLiveData<List<Article>>()
    val newList : LiveData<List<Article>> = _newsList


    // 加载状态，后面下拉刷新会用到
    private val _loading = MutableLiveData<Boolean>()
    val loading : LiveData<Boolean> = _loading

    // 错误信息，方便后面提示用户
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage : LiveData<String?> = _errorMessage

    fun loadNews(){

        viewModelScope.launch {

            _loading.value = true
            _errorMessage.value = null

            try {
                val articles = NewsRepository.getTopHeadlines(NewsConstants.apiKey)
                _newsList.value = articles
            }catch (e: Exception){
                e.printStackTrace()
                _errorMessage.value = "新闻加载失败：${e.message}"
            }finally {
                _loading.value = false
            }
        }
    }
}