package com.example.nutrition_app.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.nutrition_app.model.AppDatabase
import com.example.nutrition_app.model.entities.User
import kotlinx.coroutines.launch

class ProfileViewModel(private val db: AppDatabase) : ViewModel() {
    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user

    private val _updateResult = MutableLiveData<Result<Unit>>()
    val updateResult: LiveData<Result<Unit>> = _updateResult

    fun loadUser(context: Context) {
        viewModelScope.launch {
            val userId = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
                .getInt("userId", -1)
            if (userId != -1) {
                _user.value = db.userDao().getUserById(userId)
            }
        }
    }

    fun updateUser(context: Context, age: String, weight: String, goal: String, email: String) {
        viewModelScope.launch {
            val userId = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
                .getInt("userId", -1)
            if (userId == -1) {
                _updateResult.value = Result.failure(Exception("Пользователь не найден"))
                return@launch
            }

            val user = db.userDao().getUserById(userId) ?: run {
                _updateResult.value = Result.failure(Exception("Пользователь не найден"))
                return@launch
            }

            val ageInt = age.toIntOrNull()
            if (ageInt == null || ageInt <= 18 || ageInt > 100) {
                _updateResult.value = Result.failure(Exception("Некорректный возраст"))
                return@launch
            }
            val weightFloat = weight.toFloatOrNull()
            if (weightFloat == null || weightFloat <= 30 || weightFloat > 300) {
                _updateResult.value = Result.failure(Exception("Некорректный вес"))
                return@launch
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                _updateResult.value = Result.failure(Exception("Некорректный email"))
                return@launch
            }
            if (goal !in listOf("Поддержание веса", "Снижение веса", "Набор веса")) {
                _updateResult.value = Result.failure(Exception("Выберите цель"))
                return@launch
            }

            val existingUser = db.userDao().getUserByEmail(email)
            if (existingUser != null && existingUser.id != userId) {
                _updateResult.value = Result.failure(Exception("Email уже занят"))
                return@launch
            }

            val updatedUser = user.copy(
                age = ageInt,
                weight = weightFloat,
                goal = goal,
                email = email
            )
            db.userDao().update(updatedUser)
            _user.value = updatedUser
            _updateResult.value = Result.success(Unit)
        }
    }

    fun logout(context: Context) {
        context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
            .edit().remove("userId").apply()
    }

    class Factory(private val db: AppDatabase) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ProfileViewModel(db) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
