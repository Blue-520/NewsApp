package com.blue.newsapp.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import com.blue.newsapp.data.loacl.database.UserPreferences
import com.blue.newsapp.data.loacl.entity.FavoriteNewsEntity
import com.blue.newsapp.repository.NewLocalRepository
import kotlinx.coroutines.flow.first

class FavoriteNewsViewModel(application: Application) : AndroidViewModel(application){

   private val reposity = NewLocalRepository.getInstance(application)

   private val userPreferences = UserPreferences(application)

   private val currentUserId = userPreferences.userIdFlow.asLiveData()

   val favoriteNewsList: LiveData<List<FavoriteNewsEntity>> = currentUserId.switchMap { userId ->
       if (userId == -1L) {
           MutableLiveData(emptyList())
       } else {
           reposity.getAllFavoriteNewsByUserId(userId)
       }
   }


   /**
    * 判断当前是否已登录
    */
   suspend fun isLogin(): Boolean {
       return userPreferences.isLoginFlow.first()
   }
}