package com.blue.newsapp.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blue.newsapp.repository.UserReposity
import kotlinx.coroutines.launch

class RegisterViewModel(private val userReposity: UserReposity): ViewModel() {

    // 注册结果消息
    private val _registerMessage = MutableLiveData<String>()
    val registerMessage: LiveData<String> = _registerMessage

    // 是否注册成功
    private val _registerSuccess = MutableLiveData<Boolean>()
    val registerSuccess: LiveData<Boolean> = _registerSuccess

    /**
     * 注册
     */
    fun register(username: String, password: String, confirmPassword: String){
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()){
            _registerMessage.value = "请输入完整信息"
            return
        }

        if (password != confirmPassword){
            _registerMessage.value = "两次输入的密码不一致"
            return
        }

        viewModelScope.launch {
            val result = userReposity.register(username, password)
            _registerMessage.value = result.second
            _registerSuccess.value = result.first
        }
    }
}