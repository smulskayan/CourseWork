package com.example.nutrition_app.model.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "food_diary", foreignKeys = [
    ForeignKey(entity = User::class, parentColumns = ["id"], childColumns = ["userId"]),
    ForeignKey(entity = Recipe::class, parentColumns = ["id"], childColumns = ["recipeId"]),
])
data class FoodDiary(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val recipeId: Int?,
    val foodId: Int?,
    val quantity: Float,
    val calories: Float,
    val protein: Float,
    val fat: Float,
    val carbs: Float,
    val waterAmount: Float,
    val diaryDate: String,
    val diaryTime: String
)