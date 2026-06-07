package com.blue.newsapp.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.blue.newsapp.data.loacl.database.SessionManager
import com.blue.newsapp.data.loacl.entity.UserEntity
import com.blue.newsapp.repository.AuthRepository
import com.blue.newsapp.repository.UserReposity
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@HiltViewModel
class MineViewModel @Inject constructor(private val userRepository: UserReposity, private val sessionManager: SessionManager) : ViewModel() {
    val isLogin: LiveData<Boolean> = sessionManager.isLoginFlow.asLiveData()

    private val currentUserId: LiveData<Long> = sessionManager.userIdFlow.asLiveData()

    val userProfile: LiveData<UserEntity?> = currentUserId.switchMap { userId ->
        if (userId == -1L) {
            MutableLiveData<UserEntity?>(null)
        } else {
            userRepository.observeUserById(userId)
        }
    }

    private val _message = MutableLiveData<String?>()
    val message: LiveData<String?> = _message

    fun logout(){
        viewModelScope.launch {
            sessionManager.clearSession()
        }
    }

    fun updateUsername(username: String){
        if (username.isBlank()){
            _message.value = "用户名不能为空"
            return
        }
        viewModelScope.launch {
            val userId = sessionManager.userIdFlow.first()
            if (userId == -1L) return@launch
            val result = userRepository.updateUsername(userId, username.trim())
            if (result.first) {
                sessionManager.updateUsername(username.trim())
            }
            _message.value = result.second
        }
    }

    fun updateSignature(signature: String){
        viewModelScope.launch {
            val userId = sessionManager.userIdFlow.first()
            if (userId == -1L) return@launch
            userRepository.updateSignature(userId, signature.trim())
            _message.value = "个性签名已更新"
        }
    }

    fun updateAvatar(avatar: String){
        viewModelScope.launch {
            val userId = sessionManager.userIdFlow.first()
            if (userId == -1L) return@launch
            userRepository.updateAvatar(userId, avatar)
            _message.value = "头像已更新"
        }
    }

    fun updateBackgroundImage(backgroundImage: String){
        viewModelScope.launch {
            val userId = sessionManager.userIdFlow.first()
            if (userId == -1L) return@launch
            userRepository.updateBackgroundImage(userId, backgroundImage)
            _message.value = "背景图已更新"
        }
    }

    fun clearMessage() {
        _message.value = null
    }
}
