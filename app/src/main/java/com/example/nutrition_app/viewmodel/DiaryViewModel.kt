package com.example.nutrition_app.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.nutrition_app.model.AppDatabase
import com.example.nutrition_app.model.dao.FoodDiaryDao
import com.example.nutrition_app.model.entities.FoodDiary
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DiaryViewModel(
    private val foodDiaryDao: FoodDiaryDao,
    private val userId: Int
) : ViewModel() {

    val diaryEntries: LiveData<List<FoodDiary>> = foodDiaryDao.getAllEntries(userId)

    fun addWaterEntry(waterMl: Float) {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val currentDate = dateFormat.format(Date())
        val currentTime = timeFormat.format(Date())

        val entry = FoodDiary(
            userId = userId,
            recipeId = null,
            foodId = null,
            quantity = 0f,
            calories = 0f,
            protein = 0f,
            fat = 0f,
            carbs = 0f,
            waterAmount = waterMl,
            diaryDate = currentDate,
            diaryTime = currentTime
        )
        viewModelScope.launch {
            foodDiaryDao.insert(entry)
        }
    }

    fun addFoodEntry(
        grams: Float,
        calories: Float,
        protein: Float,
        fat: Float,
        carbs: Float
    ) {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val currentDate = dateFormat.format(Date())
        val currentTime = timeFormat.format(Date())

        val entry = FoodDiary(
            userId = userId,
            recipeId = null,
            foodId = null,
            quantity = grams,
            calories = calories,
            protein = protein,
            fat = fat,
            carbs = carbs,
            waterAmount = 0f,
            diaryDate = currentDate,
            diaryTime = currentTime
        )
        viewModelScope.launch {
            foodDiaryDao.insert(entry)
        }
    }

    class Factory(private val context: Context, private val userId: Int) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DiaryViewModel::class.java)) {
                val db = AppDatabase.getDatabase(context)
                @Suppress("UNCHECKED_CAST")
                return DiaryViewModel(db.foodDiaryDao(), userId) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}