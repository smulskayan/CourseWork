package com.example.nutrition_app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.nutrition_app.model.AppDatabase
import com.example.nutrition_app.model.entities.User
import kotlinx.coroutines.launch

class RegisterViewModel(private val db: AppDatabase) : ViewModel() {
    private val _registrationResult = MutableLiveData<Result<Int>>()
    val registrationResult: LiveData<Result<Int>> = _registrationResult

    private val _emailError = MutableLiveData<String?>()
    val emailError: LiveData<String?> = _emailError

    private val _passwordError = MutableLiveData<String?>()
    val passwordError: LiveData<String?> = _passwordError

    fun register(
        firstName: String,
        gender: String,
        age: Int,
        height: String,
        weight: String,
        email: String,
        password: String,
        confirmPassword: String,
        goal: String
    ) {
        viewModelScope.launch {
            // Валидация
            if (firstName.isBlank()) {
                _registrationResult.value = Result.failure(Exception("Имя не может быть пустым"))
                return@launch
            }
            if (gender !in listOf("Мужской", "Женский")) {
                _registrationResult.value = Result.failure(Exception("Выберите пол"))
                return@launch
            }
            val heightFloat = height.toFloatOrNull()
            if (heightFloat == null || heightFloat <= 0) {
                _registrationResult.value = Result.failure(Exception("Некорректный рост"))
                return@launch
            }
            val weightFloat = weight.toFloatOrNull()
            if (weightFloat == null || weightFloat <= 0) {
                _registrationResult.value = Result.failure(Exception("Некорректный вес"))
                return@launch
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                _emailError.value = "Некорректный email"
                _registrationResult.value = Result.failure(Exception("Некорректный email"))
                return@launch
            }
            if (password.length < 6) {
                _passwordError.value = "Пароль должен содержать минимум 6 символов"
                _registrationResult.value = Result.failure(Exception("Некорректный пароль"))
                return@launch
            }
            if (password != confirmPassword) {
                _passwordError.value = "Пароли не совпадают"
                _registrationResult.value = Result.failure(Exception("Пароли не совпадают"))
                return@launch
            }
            if (goal !in listOf("Поддержание веса", "Снижение веса", "Набор веса")) {
                _registrationResult.value = Result.failure(Exception("Выберите цель"))
                return@launch
            }

            // Проверка уникальности email
            val existingUser = db.userDao().getUserByEmail(email)
            if (existingUser != null) {
                _emailError.value = "Email уже занят"
                _registrationResult.value = Result.failure(Exception("Email уже занят"))
                return@launch
            }

            // Сохранение пользователя
            val user = User(
                firstName = firstName,
                gender = gender,
                age = age,
                height = heightFloat,
                weight = weightFloat,
                email = email,
                password = password,
                goal = goal,
            )
            db.userDao().insert(user)
            val userId = db.userDao().getUserByEmail(email)?.id ?: -1
            _registrationResult.value = Result.success(userId)
        }
    }

    class Factory(private val db: AppDatabase) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return RegisterViewModel(db) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}