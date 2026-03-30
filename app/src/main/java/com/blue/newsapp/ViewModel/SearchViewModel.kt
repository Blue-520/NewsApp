package com.blue.newsapp.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.blue.newsapp.repository.NewsRepository
import com.blue.newsapp.repository.SearchNewsPagingSource
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest

@HiltViewModel
class SearchViewModel @Inject constructor(private val newsRepository: NewsRepository): ViewModel() {

    private val queryFlow = MutableStateFlow("")

    val searchFlow = queryFlow.filter { it.isNotBlank() }
        .distinctUntilChanged()
        .flatMapLatest { query ->
            Pager(
                config = PagingConfig(pageSize = 5, prefetchDistance = 2, enablePlaceholders = false),
                pagingSourceFactory = {
                    SearchNewsPagingSource(newsRepository, query)
                }
            ).flow.cachedIn(viewModelScope)
        }

    fun search(query: String){
        queryFlow.value = query.trim()
    }
}