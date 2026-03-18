package com.blue.newsapp.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blue.newsapp.data.model.Article
import com.blue.newsapp.repository.NewsRepository
import kotlinx.coroutines.launch

class SearchViewModel: ViewModel() {
    // 搜索结果列表
    private val _searchResult = MutableLiveData<List<Article>>()
    val searchResult : LiveData<List<Article>> = _searchResult

    // 错误信息
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage : LiveData<String?> = _errorMessage

    // 加载状态
    private val _loading = MutableLiveData<Boolean>()
    val loading : LiveData<Boolean> = _loading

    /**
     * 执行搜索
     */
    fun search(query: String){

        // 关键词为空时，直接不搜索
        if (query.isBlank()){
            _errorMessage.value = "请输入关键词"
            return
        }

        viewModelScope.launch {
            _loading.value = true

            val result = NewsRepository.getSearch(query)

            result.onSuccess { articles ->
                _searchResult.value = articles
                _errorMessage.value = null
            }.onFailure { throwable ->
                _errorMessage.value = throwable.message ?: "搜索失败"
            }

            _loading.value = false
        }
    }
}