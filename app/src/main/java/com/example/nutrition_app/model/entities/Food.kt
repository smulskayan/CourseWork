package com.example.nutrition_app.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "food")
data class Food(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val icon: String?,
    val carbs: Float,
    val calories: Float,
    val protein: Float,
    val fat: Float
)