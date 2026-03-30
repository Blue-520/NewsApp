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
class RegisterViewModel @Inject constructor(private val authReposity: AuthRepository): ViewModel() {

    private val _registerState = MutableLiveData<UiState<Unit>>()
    val registerState : LiveData<UiState<Unit>> = _registerState

    /**
     * 注册
     */
    fun register(username: String, password: String, confirmPassword: String){

        if (username.isBlank()){
            _registerState.value = UiState.Error("请输入账号")
        }

        if (password.isBlank()){
            _registerState.value = UiState.Error("请输入密码")
        }

        if (confirmPassword.isBlank()){
            _registerState.value = UiState.Error("请输入确认密码")
        }

        if (password != confirmPassword) {
            _registerState.value = UiState.Error("两次输入的密码不一致")
            return
        }

        viewModelScope.launch {
            _registerState.value = UiState.Loading

            val result = authReposity.register(username, password)

            result.fold(
                onSuccess = {
                    _registerState.value = UiState.Success(Unit)
                }, onFailure = { exception ->
                    _registerState.value = UiState.Error(exception.message ?: "注册失败")
                }
            )

        }
    }
}