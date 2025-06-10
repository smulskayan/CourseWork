package com.example.nutrition_app.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.nutrition_app.model.AppDatabase
import com.example.nutrition_app.model.dao.DailySummary
import com.example.nutrition_app.model.entities.User
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RecommendationsViewModel(private val database: AppDatabase) : ViewModel() {

    private val _dailySummary = MutableLiveData<DailySummary>()
    val dailySummary: LiveData<DailySummary> = _dailySummary

    private val _recommendations = MutableLiveData<Recommendations>()
    val recommendations: LiveData<Recommendations> = _recommendations

    fun loadRecommendations(userId: Int) {
        viewModelScope.launch {
            // Получение данных пользователя
            val user = database.userDao().getUserById(userId)
            if (user == null) {
                _dailySummary.value = DailySummary(0f, 0f, 0f, 0f, 0f)
                _recommendations.value = Recommendations(0f, 0f)
                return@launch
            }

            // Получение статистики за сегодня
            val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            val today = dateFormat.format(Date())
            val summary = database.foodDiaryDao().getDailySummary(userId, today)
                ?: DailySummary(0f, 0f, 0f, 0f, 0f)
            _dailySummary.value = summary

            // Расчет BMR
            val bmr = if (user.gender == "male") {
                10 * user.weight + 6.25f * user.height - 5f * user.age + 5f
            } else {
                10 * user.weight + 6.25f * user.height - 5f * user.age - 161f
            }

            // Расчет калорий по цели
            val targetCalories = when (user.goal) {
                "lose" -> bmr * 0.8f
                "gain" -> bmr * 1.4f
                else -> bmr * 1.2f // maintain
            }

            // Норма воды: 30 мл/кг
            val targetWater = user.weight * 30

            // Остаток
            val caloriesRemaining = (targetCalories - summary.totalCalories).coerceAtLeast(0f)
            val waterRemaining = (targetWater - summary.totalWater).coerceAtLeast(0f)

            _recommendations.value = Recommendations(caloriesRemaining, waterRemaining)
        }
    }

    data class Recommendations(
        val caloriesRemaining: Float,
        val waterRemaining: Float
    )

    class Factory(private val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(RecommendationsViewModel::class.java)) {
                val db = AppDatabase.getDatabase(context)
                @Suppress("UNCHECKED_CAST")
                return RecommendationsViewModel(db) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}