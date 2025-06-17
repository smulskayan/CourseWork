package com.example.nutrition_app.views

import com.example.nutrition_app.model.entities.FoodDiary

data class DayEntry(
    val date: String,
    val entries: List<FoodDiary>
)
