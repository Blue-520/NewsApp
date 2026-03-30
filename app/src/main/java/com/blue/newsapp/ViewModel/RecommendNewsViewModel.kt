package com.blue.newsapp.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blue.newsapp.data.loacl.database.SessionManager
import com.blue.newsapp.data.loacl.entity.NewsEntity
import com.blue.newsapp.repository.NewLocalRepository
import com.blue.newsapp.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class RecommendNewsViewModel @Inject constructor(private val newLocalRepository: NewLocalRepository, private val sessionManager: SessionManager) : ViewModel(){

    private val _uiState = MutableLiveData<UiState<List<NewsEntity>>>()
    val uiState: LiveData<UiState<List<NewsEntity>>> = _uiState

    private val currentUserId = MutableLiveData<Long>()

    init {
        observeUserId()

    }

    private fun observeUserId(){
        viewModelScope.launch {
            sessionManager.userIdFlow.collect { userId ->
                currentUserId.postValue(userId)
                loadNews()
            }
        }
    }

    fun loadNews(){
        _uiState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val newsList = newLocalRepository.getRecommendNewsByRatio(currentUserId.value ?: -1L)

                if (newsList.isNotEmpty()){ _uiState.value = UiState.Success(newsList)
                }else{
                    _uiState.value = UiState.Empty("暂无推荐新闻")
                }
            }catch (e: Exception){
                e.printStackTrace()
                _uiState.value = UiState.Error("新闻加载失败:${e.message}")
            }
        }
    }

    //加分
    fun increaseScore(category: String){
        viewModelScope.launch {
            newLocalRepository.increaseUserInterestScore(currentUserId.value ?: -1, category, 1)
        }
    }
}