package com.selavu.app.data.security

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class SessionManager(context: Context) {

    private val prefs: SharedPreferences = createEncryptedPrefs(context)

    companion object {
        private const val TAG = "SessionManager"
        private const val PREF_FILE_NAME = "selavu_secure_session"
        private const val FALLBACK_FILE_NAME = "selavu_session_fallback"

        private const val KEY_IS_LOGGED_IN = "key_is_logged_in"
        private const val KEY_USER_ID = "key_user_id"
        private const val KEY_USER_EMAIL = "key_user_email"
        private const val KEY_USER_AGE = "key_user_age"

        @Volatile
        private var INSTANCE: SessionManager? = null

        fun getInstance(context: Context): SessionManager {
            return INSTANCE ?: synchronized(this) {
                val instance = SessionManager(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }

        private fun createEncryptedPrefs(context: Context): SharedPreferences {
            return try {
                val masterKey = MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build()

                EncryptedSharedPreferences.create(
                    context,
                    PREF_FILE_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                )
            } catch (e: Exception) {
                Log.w(TAG, "EncryptedSharedPreferences unavailable, falling back to standard prefs: ${e.message}")
                context.getSharedPreferences(FALLBACK_FILE_NAME, Context.MODE_PRIVATE)
            }
        }
    }

    fun saveUserSession(userId: Long, email: String, age: Int) {
        prefs.edit()
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .putLong(KEY_USER_ID, userId)
            .putString(KEY_USER_EMAIL, email)
            .putInt(KEY_USER_AGE, age)
            .apply()
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false) && getUserId() > 0
    }

    fun getUserId(): Long {
        return prefs.getLong(KEY_USER_ID, -1L)
    }

    fun getUserEmail(): String {
        return prefs.getString(KEY_USER_EMAIL, "") ?: ""
    }

    fun getUserAge(): Int {
        return prefs.getInt(KEY_USER_AGE, 0)
    }

    fun clearSession() {
        prefs.edit()
            .clear()
            .apply()
    }
}

