package com.example.nutrition_app.views

import com.example.nutrition_app.model.entities.FoodDiary

data class DayEntry(
    val date: String, // или Date, если diaryDate — это Date
    val entries: List<FoodDiary>
)
