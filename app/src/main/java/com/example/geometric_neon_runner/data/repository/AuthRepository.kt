package com.example.geometric_neon_runner.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.geometric_neon_runner.data.model.*
import com.example.geometric_neon_runner.data.remote.FirestoreSource
import com.example.geometric_neon_runner.data.remote.FirebaseAuthSource
import com.google.firebase.firestore.auth.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository(
        private val context: Context,
        private val authSource: FirebaseAuthSource = FirebaseAuthSource(firestoreSource = FirestoreSource()),
        private val firestoreSource: FirestoreSource = FirestoreSource()
) {

    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    companion object {
        private const val KEY_UID = "uid"
        private const val KEY_USERNAME = "username"
        private const val KEY_EMAIL = "email"
    }

    suspend fun register(email: String, password: String, username: String): Result<User> {
        return withContext(Dispatchers.IO) {
            val res = authSource.register(email, password, username)
            if (res is Result.Success) {
                saveUserLocally(res.data)
            }
            res
        }
    }

    suspend fun login(email: String, password: String): Result<User> {
        return withContext(Dispatchers.IO) {
            val res = authSource.login(email, password)
            if (res is Result.Success) {
                saveUserLocally(res.data)
            }
            res
        }
    }

    fun logout() {
        authSource.logout()
        clearUserLocally()
    }

    suspend fun resetPassword(email: String): Result<Unit> {
        return authSource.resetPassword(email)
    }

    fun isUserLoggedIn(): Boolean = authSource.isUserLoggedIn()

    fun getCurrentUserId(): String? {
        return prefs.getString(KEY_UID, null) ?: authSource.currentUser?.uid
    }

    fun getCurrentUsername(): String {
        return prefs.getString(KEY_USERNAME, "") ?: ""
    }

    fun saveUserLocally(user: User) {
        prefs.edit()
                .putString(KEY_UID, user.uid)
                .putString(KEY_USERNAME, user.username)
                .putString(KEY_EMAIL, user.email)
                .apply()
    }

    fun clearUserLocally() {
        prefs.edit().clear().apply()
    }
}
