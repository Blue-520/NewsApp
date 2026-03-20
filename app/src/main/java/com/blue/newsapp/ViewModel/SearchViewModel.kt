package com.blue.newsapp.ViewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blue.newsapp.data.model.Article
import com.blue.newsapp.repository.NewLocalRepository
import com.blue.newsapp.repository.NewsRepository
import kotlinx.coroutines.launch

class SearchViewModel(application: Application): AndroidViewModel(application) {

    private val repository = NewLocalRepository.getInstance(application)

    // 搜索结果列表
    private val _searchResultList = MutableLiveData<List<Article>>()
    val searchResultList : LiveData<List<Article>> = _searchResultList

    // 错误信息
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage : LiveData<String?> = _errorMessage

    // 加载状态
    private val _loading = MutableLiveData<Boolean>()
    val loading : LiveData<Boolean> = _loading

    /**
     * 执行搜索
     */
    fun searchNews(query: String){

        // 关键词为空时，直接不搜索
        if (query.isBlank()){
            _searchResultList.value = emptyList()
            _errorMessage.value = "请输入关键词"
            return
        }

        viewModelScope.launch {
            _loading.value = true
            _errorMessage.value = ""

            try {
                val result = NewsRepository.getSearch(query)
                result.onSuccess { articles ->
                    _searchResultList.value = articles
                    _errorMessage.value = null
                }.onFailure { throwable ->
                    _searchResultList.value = emptyList()
                    _errorMessage.value = throwable.message ?: "搜索失败"
                }
            }catch (e: Exception){
                e.printStackTrace()
                Log.d("aaa", "${e.message}")
                _errorMessage.value = e.message ?: "搜索失败"
            }finally {
                _loading.value = false
            }
        }
    }

    fun saveNews(news: Article, catrgory: String){
        viewModelScope.launch {
            repository.saveNews(news, catrgory)
        }
    }
}