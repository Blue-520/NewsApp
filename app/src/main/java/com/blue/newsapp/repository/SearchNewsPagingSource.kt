package com.blue.newsapp.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.blue.newsapp.data.model.Article
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class SearchNewsPagingSource @Inject constructor(private val newsRepository: NewsRepository, private val query: String): PagingSource<Int, Article>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {
        return try {
            val currentPage = params.key ?: 1

            val result = newsRepository.getSearch(query)

            result.fold(
                onSuccess = { newsList ->
                    LoadResult.Page(
                        data = newsList,
                        prevKey = if (currentPage == 1) null else currentPage - 1,
                        nextKey = if (newsList.isEmpty()) null else currentPage + 1)
                }, onFailure = { throwable ->
                    LoadResult.Error(throwable)
                }
            )
        }catch (e: Exception){
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Article>): Int? {
        // anchorPosition = 当前屏幕上用户最近访问的位置
        val anchorPosition = state.anchorPosition ?: return null

        // 找到离这个位置最近的那一页
        val anchorPage = state.closestPageToPosition(anchorPosition) ?: return null

        // 推算刷新时应该从哪一页开始重新加载
        return anchorPage.prevKey?.plus(1) ?: anchorPage.nextKey?.minus(1)
    }
}