package com.blue.newsapp.data.loacl.database

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

// 给 Context 扩展一个 dataStore 属性
private val Context.dataStore by preferencesDataStore(name = "session")

@Singleton
class SessionManager @Inject constructor(@ApplicationContext private val context: Context) {

    companion object {
        private val KEY_IS_LOGIN = booleanPreferencesKey("is_login")
        private val KEY_USER_ID = longPreferencesKey("user_id")
        private val KEY_USERNAME = stringPreferencesKey("username")
        private val KEY_ACCESS_TOKEN = stringPreferencesKey("access_token")
        private val KEY_REFRESH_TOKEN = stringPreferencesKey("refresh_token")
    }

    val sessionFlow: Flow<UserSession> = context.dataStore.data.map { prefs ->
        UserSession(
            isLogin = prefs[KEY_IS_LOGIN] ?: false,
            userId = prefs[KEY_USER_ID] ?: -1L,
            username = prefs[KEY_USERNAME] ?: "",
            accessToken = prefs[KEY_ACCESS_TOKEN] ?: "",
            refreshToken = prefs[KEY_REFRESH_TOKEN] ?: ""
        )
    }

    val userIdFlow: Flow<Long> = sessionFlow.map { it.userId }

    val isLoginFlow: Flow<Boolean> = sessionFlow.map { it.isLogin }

    suspend fun saveLoginSession(userId: Long, username: String, accessToken: String, refreshToken: String){
        context.dataStore.edit { prefs ->
            prefs[KEY_IS_LOGIN] = true
            prefs[KEY_USER_ID] = userId
            prefs[KEY_USERNAME] = username
            prefs[KEY_ACCESS_TOKEN] = accessToken
            prefs[KEY_REFRESH_TOKEN] = refreshToken
        }
    }

    suspend fun updateTokens(accessToken: String, refreshToken: String?){
        context.dataStore.edit { prefs ->
            prefs[KEY_ACCESS_TOKEN] = accessToken
            if (!refreshToken.isNullOrEmpty()){
                prefs[KEY_REFRESH_TOKEN] = refreshToken
            }
        }
    }

    suspend fun updateUsername(username: String){
        context.dataStore.edit { prefs ->
            prefs[KEY_USERNAME] = username
        }
    }

    suspend fun clearSession(){
        context.dataStore.edit { prefs ->
            prefs[KEY_IS_LOGIN] = false
            prefs[KEY_USER_ID] = -1L
            prefs[KEY_USERNAME] = ""
            prefs[KEY_REFRESH_TOKEN] = ""
            prefs[KEY_ACCESS_TOKEN] = ""
        }
    }

    suspend fun getAccessToken(): String = sessionFlow.first().accessToken

    suspend fun getRefreshToken(): String = sessionFlow.first().refreshToken

    suspend fun getUserId(): Long = sessionFlow.first().userId

    suspend fun isLogin(): Boolean = sessionFlow.first().isLogin

    data class UserSession(
        val isLogin: Boolean,
        val userId: Long,
        val username: String,
        val accessToken: String,
        val refreshToken: String
    )
}
