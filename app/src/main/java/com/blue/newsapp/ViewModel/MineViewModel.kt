package com.blue.newsapp.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.blue.newsapp.data.loacl.database.UserPreferences
import kotlinx.coroutines.launch

class MineViewModel(private val userPreferences: UserPreferences) : ViewModel() {

    // 是否已登录
    val isLogin: LiveData<Boolean> = userPreferences.isLoginFlow.asLiveData()

    //当前用户名
    val username: LiveData<String> = userPreferences.usernameFlow.asLiveData()

    fun logout(){
        viewModelScope.launch {
            userPreferences.clearLoginUser()
        }
    }
}