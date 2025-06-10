package com.example.nutrition_app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.nutrition_app.model.AppDatabase
import com.example.nutrition_app.model.entities.User
import kotlinx.coroutines.launch

class LoginViewModel(private val db: AppDatabase) : ViewModel() {
    private val _loginResult = MutableLiveData<Result<Int>>()
    val loginResult: LiveData<Result<Int>> = _loginResult

    private val _emailError = MutableLiveData<String?>()
    val emailError: LiveData<String?> = _emailError

    private val _passwordError = MutableLiveData<String?>()
    val passwordError: LiveData<String?> = _passwordError

    fun login(email: String, password: String) {
        viewModelScope.launch {
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                _emailError.value = "Некорректный email"
                _loginResult.value = Result.failure(Exception("Некорректный email"))
                return@launch
            }
            if (password.isBlank()) {
                _passwordError.value = "Введите пароль"
                _loginResult.value = Result.failure(Exception("Введите пароль"))
                return@launch
            }

            val user = db.userDao().getUser(email, password)
            if (user != null) {
                _loginResult.value = Result.success(user.id)
            } else {
                _passwordError.value = "Неверный email или пароль"
                _loginResult.value = Result.failure(Exception("Неверный email или пароль"))
            }
        }
    }

    class Factory(private val db: AppDatabase) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return LoginViewModel(db) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}