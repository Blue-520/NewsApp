package com.blue.newsapp.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blue.newsapp.data.loacl.database.UserPreferences
import com.blue.newsapp.repository.UserReposity
import kotlinx.coroutines.launch

class LoginViewModel(private val userRepository: UserReposity, private val userPreferences: UserPreferences) : ViewModel(){

    //登录结果消息
    private val _loginMessage = MutableLiveData<String>()
    val loginMessage : LiveData<String> = _loginMessage

    //是否登录成功
    private val _loginSuccess = MutableLiveData<Boolean>()
    val loginSuccess : LiveData<Boolean> = _loginSuccess

    /**
     * 登录
     */
    fun login(username: String, password: String){
        if (username.isEmpty() || password.isEmpty()){
            _loginMessage.value = "请输入用户名或者密码"
        }

        viewModelScope.launch {

            val user = userRepository.login(username, password)

            if (user != null){
                // 保存登录状态到 DataStore
                userPreferences.saveLoginUser(user.id, user.username)

                _loginMessage.value = "登录成功"
                _loginSuccess.value = true
            }else{
                _loginMessage.value = "用户名或密码错误"
                _loginSuccess.value = false
            }
        }
    }
}