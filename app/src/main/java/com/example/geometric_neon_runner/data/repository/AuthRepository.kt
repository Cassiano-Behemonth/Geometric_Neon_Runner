package com.example.geometric_neon_runner.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.geometric_neon_runner.data.model.User
import com.example.geometric_neon_runner.data.remote.FirestoreSource
import com.example.geometric_neon_runner.data.remote.FirebaseAuthSource
import com.example.geometric_neon_runner.utils.Constants
import com.example.geometric_neon_runner.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository(
    private val context: Context,
    private val authSource: FirebaseAuthSource = FirebaseAuthSource(firestoreSource = FirestoreSource()),
    private val firestoreSource: FirestoreSource = FirestoreSource()
) {

    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)
    }

    suspend fun register(email: String, password: String, username: String): Result<User> {
        return withContext(Dispatchers.IO) {
            val res = authSource.register(email, password, username)
            if (res is Result.Success) {
                saveUserLocally(res.data)
            }
            // Se falhar, presumimos que authSource.register já retorna Result.Error ou lança exceção.
            res
        }
    }

    suspend fun login(email: String, password: String): Result<User> {
        return withContext(Dispatchers.IO) {
            val res = authSource.login(email, password)

            when (res) {
                is Result.Success -> {
                    saveUserLocally(res.data)
                    res
                }
                is Result.Error -> {
                    // 🛑 NOVO: Relança a exceção do Firebase para que o ViewModel possa identificá-la.
                    // ATENÇÃO: Se o seu Result.Error não tem um campo 'exception', esta linha falhará.
                    if (res.exception != null) {
                        throw res.exception
                    } else {
                        // Se não há exceção, retorna o Result.Error original
                        res
                    }
                }
                is Result.Loading -> res
            }
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
        return prefs.getString(Constants.KEY_USER_ID, null) ?: authSource.currentUser?.uid
    }

    fun getCurrentUsername(): String {
        return prefs.getString(Constants.KEY_USERNAME, "") ?: ""
    }

    fun saveUserLocally(user: User) {
        prefs.edit()
            .putString(Constants.KEY_USER_ID, user.uid)
            .putString(Constants.KEY_USERNAME, user.username)
            .putString(Constants.KEY_EMAIL, user.email)
            .apply()
    }

    fun clearUserLocally() {
        prefs.edit().clear().apply()
    }
}