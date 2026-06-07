package com.blue.newsapp.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.blue.newsapp.repository.NewsRepository
import com.blue.newsapp.data.paging.SearchNewsPagingSource
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SearchViewModel @Inject constructor(private val newsRepository: NewsRepository): ViewModel() {

    // 保存当前搜索词
    private val queryFlow = MutableStateFlow("")

    val config = PagingConfig(pageSize = 10, prefetchDistance = 2, enablePlaceholders = false)

    // 根据搜索词动态生成分页数据流
    val searchFlow = queryFlow
        .map { it.trim() }                                              // 先去掉首尾空格
        .filter { it.isNotBlank() }                                     // 空关键词不搜索
        .distinctUntilChanged()                                         // 相同关键词不重复搜索
        .flatMapLatest { query ->                                       // 新关键词来了，取消旧搜索
            Pager(
                config = config,
                pagingSourceFactory = {
                    SearchNewsPagingSource(newsRepository, query)
                }
            ).flow
        }
        .cachedIn(viewModelScope)

    fun search(query: String){
        queryFlow.value = query.trim()
    }
}