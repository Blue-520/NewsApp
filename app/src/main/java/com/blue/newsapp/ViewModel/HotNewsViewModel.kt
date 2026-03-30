package com.blue.newsapp.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.blue.newsapp.data.network.NewsConstants
import com.blue.newsapp.repository.HotNewsPagingSource
import com.blue.newsapp.repository.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject

@HiltViewModel
class HotNewsViewModel @Inject constructor(private val newsRepository: NewsRepository) : ViewModel() {

    val hotNewsFlow = Pager(
        config = PagingConfig(pageSize = 5, prefetchDistance = 2, enablePlaceholders = false),                   //每页按5条来取，当列表距离底部还剩2条左右时就提前加载下一页，先关掉占位符。
        pagingSourceFactory = {
            HotNewsPagingSource(newsRepository = newsRepository, apiKey = NewsConstants.apiKey, category = "health")
        }        //数据源
    ).flow.cachedIn(viewModelScope)                                                                              //在 ViewModel 生命周期内缓存分页流。

}