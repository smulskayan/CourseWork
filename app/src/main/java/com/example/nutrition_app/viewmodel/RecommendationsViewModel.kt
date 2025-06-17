package com.example.nutrition_app.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.nutrition_app.model.AppDatabase
import com.example.nutrition_app.model.dao.DailySummary
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
            val user = database.userDao().getUserById(userId)
            if (user == null) {
                _dailySummary.value = DailySummary(0f, 0f, 0f, 0f, 0f)
                _recommendations.value = Recommendations(
                    caloriesRemaining = 0f,
                    waterRemaining = 0f,
                    proteinTarget = 0f,
                    fatTarget = 0f,
                    carbsTarget = 0f,
                    targetCalories = 0f,
                    targetWater = 0f
                )
                return@launch
            }

            val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            val today = dateFormat.format(Date())
            val summary = database.foodDiaryDao().getDailySummary(userId, today)
                ?: DailySummary(0f, 0f, 0f, 0f, 0f)
            _dailySummary.value = summary

            val bmr = if (user.gender == "Мужской") {
                10 * user.weight + 6.25f * user.height - 5f * user.age + 5f
            } else {
                10 * user.weight + 6.25f * user.height - 5f * user.age - 161f
            }

            val targetCalories = when (user.goal) {
                "Снижение веса" -> bmr * 0.8f
                "Набор веса" -> bmr * 1.4f
                "Поддержание веса" -> bmr * 1.2f
                else -> bmr * 1.2f
            }

            val targetWater = user.weight * 30

            val caloriesRemaining = (targetCalories - summary.totalCalories).coerceAtLeast(0f)
            val waterRemaining = (targetWater - summary.totalWater).coerceAtLeast(0f)

            val (proteinPercent, fatPercent, carbsPercent) = when (user.goal) {
                "Снижение веса" -> Triple(0.30f, 0.25f, 0.45f)
                "Набор веса" -> Triple(0.20f, 0.25f, 0.55f)
                "Поддержание веса" -> Triple(0.25f, 0.30f, 0.45f)
                else -> Triple(0.25f, 0.30f, 0.45f)
            }

            val proteinTarget = (targetCalories * proteinPercent) / 4f
            val fatTarget = (targetCalories * fatPercent) / 9f
            val carbsTarget = (targetCalories * carbsPercent) / 4f

            _recommendations.value = Recommendations(
                targetCalories = targetCalories,
                targetWater = targetWater,
                caloriesRemaining = caloriesRemaining,
                waterRemaining = waterRemaining,
                proteinTarget = proteinTarget,
                fatTarget = fatTarget,
                carbsTarget = carbsTarget
            )

        }
    }

    data class Recommendations(
        val targetCalories: Float,
        val targetWater: Float,
        val caloriesRemaining: Float,
        val waterRemaining: Float,
        val proteinTarget: Float,
        val fatTarget: Float,
        val carbsTarget: Float,
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