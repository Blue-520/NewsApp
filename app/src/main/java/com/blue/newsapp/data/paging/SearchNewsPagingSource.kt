package com.blue.newsapp.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.blue.newsapp.data.model.Article
import com.blue.newsapp.repository.NewsRepository
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class SearchNewsPagingSource @Inject constructor(private val newsRepository: NewsRepository, private val query: String): PagingSource<Int, Article>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {
        val currentPage = params.key ?: 1

        val result = newsRepository.getSearch(query)

        return try {
            result.fold(
                onSuccess = { newList ->
                    LoadResult.Page(
                        newList,
                        prevKey = if (currentPage == 1) null else currentPage - 1,
                        nextKey = if (newList.isEmpty()) null else currentPage + 1)
                },
                onFailure = { throwable ->
                    LoadResult.Error(throwable)
                }
            )
        }catch (e: Exception){
            LoadResult.Error(e)
        }
    }

    // 当 PagingSource 失效时，用于快速恢复滚动位置，简单起见可以先返回 null
    override fun getRefreshKey(state: PagingState<Int, Article>): Int? {
        val anchorPosition = state.anchorPosition ?: return null

        val anchorPage = state.closestPageToPosition(anchorPosition) ?: return null

        return anchorPage.prevKey?.plus(1) ?: anchorPage .nextKey?.minus(1)
    }
}