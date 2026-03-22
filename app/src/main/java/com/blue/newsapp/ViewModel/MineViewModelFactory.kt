package com.blue.newsapp.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.blue.newsapp.data.loacl.database.UserPreferences
import com.blue.newsapp.repository.UserReposity

class MineViewModelFactory(private val userRepository: UserReposity? = null, private val userPreference: UserPreferences? = null) : ViewModelProvider.Factory{

    @Suppress("UNCHECK_CAST")    //告诉编译器忽略类型转换警告,因为 T 是一个泛型类型，编译器无法确定 LoginViewModel 是否真的与 T 匹配。
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when{
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(userRepository ?: throw IllegalArgumentException("userReposity 不能为空"),
                    userPreference ?: throw IllegalArgumentException("userPreference 不能为空")) as T
            }

            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel(userRepository ?: throw IllegalArgumentException("userReposity 不能为空")) as T
            }

            modelClass.isAssignableFrom(MineViewModel::class.java) -> {
                MineViewModel(
                    userRepository ?: throw IllegalArgumentException("userReposity 不能为空"),
                    userPreference ?: throw IllegalArgumentException("userPreference 不能为空")
                ) as T
            }

            else -> throw IllegalArgumentException("未知的ViewModel类型")
        }
    }
}