package com.blue.newsapp.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blue.newsapp.repository.AuthRepository
import com.blue.newsapp.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class LoginViewModel @Inject constructor(private val authRepository: AuthRepository) : ViewModel(){

    // 登录页面状态
    private val _loginState = MutableLiveData<UiState<Unit>>()
    val loginState : LiveData<UiState<Unit>> = _loginState

    /**
     * 登录
     */
    fun login(username: String, password: String){

        if (username.isBlank() || password.isBlank()){
            _loginState.value = UiState.Error("请输入用户名和密码")
        }

        viewModelScope.launch {
            _loginState.value = UiState.Loading

            val result = authRepository.login(username, password)

            result.fold(
                onSuccess = {
                    _loginState.value = UiState.Success(Unit)
                },
                onFailure = { exception ->
                    _loginState.value = UiState.Error(exception.message ?: "登陆失败")
                }
            )
        }
    }
}