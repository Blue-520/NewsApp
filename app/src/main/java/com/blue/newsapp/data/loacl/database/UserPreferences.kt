package com.blue.newsapp.data.loacl.database

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// 给 Context 扩展一个 dataStore 属性
private val Context.dataStore by preferencesDataStore(name = "user_prefs")

class UserPreferences(context: Context) {

    private val appContext = context.applicationContext

    companion object {
        private val KEY_IS_LOGIN = booleanPreferencesKey("is_login")
        private val KEY_USER_ID = longPreferencesKey("user_id")
        private val KEY_USERNAME = stringPreferencesKey("username")
    }

    /**
     * 是否已登录
     */
    val isLoginFlow: Flow<Boolean> = appContext.dataStore.data.map { preferences ->
        preferences[KEY_IS_LOGIN] ?: false
    }

    /**
     * 当前登录用户 id
     */
    val userIdFlow: Flow<Long> = appContext.dataStore.data.map { preferences ->
        preferences[KEY_USER_ID] ?: -1L
    }

    /**
     * 当前登录用户名
     */
    val usernameFlow: Flow<String> = appContext.dataStore.data.map { preferences ->
        preferences[KEY_USERNAME] ?: ""
    }

    /**
     * 保存登录状态
     */
    suspend fun saveLoginUser(userId: Long, username: String) {
        appContext.dataStore.edit { preferences ->
            preferences[KEY_IS_LOGIN] = true
            preferences[KEY_USER_ID] = userId
            preferences[KEY_USERNAME] = username
        }
    }

    /**
     * 清除登录状态（退出登录）
     */
    suspend fun clearLoginUser() {
        appContext.dataStore.edit { preferences ->
            preferences[KEY_IS_LOGIN] = false
            preferences[KEY_USER_ID] = -1L
            preferences[KEY_USERNAME] = ""
        }
    }
}