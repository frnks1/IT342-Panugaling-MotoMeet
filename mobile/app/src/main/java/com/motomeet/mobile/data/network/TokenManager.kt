package com.motomeet.mobile.data.network

import android.content.Context
import android.content.SharedPreferences

class TokenManager private constructor(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("motomeet_prefs", Context.MODE_PRIVATE)

    companion object {
        @Volatile
        private var INSTANCE: TokenManager? = null

        fun getInstance(context: Context): TokenManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: TokenManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    fun saveToken(token: String) {
        prefs.edit().putString("auth_token", token).apply()
    }

    fun getToken(): String? {
        return prefs.getString("auth_token", null)
    }

    fun clearToken() {
        prefs.edit().remove("auth_token").apply()
    }
}
