package com.blue.newsapp.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blue.newsapp.data.remote.model.response.FavoriteNewsResponse
import com.blue.newsapp.data.loacl.database.SessionManager
import com.blue.newsapp.repository.FavoriteRepository
import com.blue.newsapp.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class FavoriteNewsViewModel @Inject constructor(private val favoriteRepository: FavoriteRepository, private val sessionManager: SessionManager) : ViewModel(){

   private val _uiState = MutableLiveData<UiState<List<FavoriteNewsResponse>>>()
   val uiState: LiveData<UiState<List<FavoriteNewsResponse>>> = _uiState

   fun loadFavorites(){
       viewModelScope.launch {
           _uiState.value = UiState.Loading

           if (!sessionManager.isLogin()){
               _uiState.value = UiState.Empty("请先登录再查看收藏")
               return@launch
           }

           val result = favoriteRepository.getFavorites()

           result.onSuccess { list ->
               _uiState.value = if (list.isEmpty()){
                   UiState.Empty("暂无收藏")
               }else{
                   UiState.Success(list)
               }
           }.onFailure { e ->
               _uiState.value = UiState.Error(e.message ?: "加载收藏失败")
           }
       }
   }
}