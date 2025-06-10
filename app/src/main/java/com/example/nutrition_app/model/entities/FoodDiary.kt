package com.example.nutrition_app.model.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "food_diary", foreignKeys = [
    ForeignKey(entity = User::class, parentColumns = ["id"], childColumns = ["userId"]),
    ForeignKey(entity = Recipe::class, parentColumns = ["id"], childColumns = ["recipeId"]),
    ForeignKey(entity = Food::class, parentColumns = ["id"], childColumns = ["foodId"])
])
data class FoodDiary(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val recipeId: Int?,
    val foodId: Int?,
    val quantity: Float, // Граммы для еды
    val calories: Float, // Калории для еды
    val protein: Float, // Белки (г) для еды
    val fat: Float, // Жиры (г) для еды
    val carbs: Float, // Углеводы (г) для еды
    val waterAmount: Float, // Количество воды (мл)
    val diaryDate: String,
    val diaryTime: String
)