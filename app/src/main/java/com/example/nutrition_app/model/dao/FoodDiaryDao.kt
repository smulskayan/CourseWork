package com.example.nutrition_app.model.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.nutrition_app.model.entities.FoodDiary

@Dao
interface FoodDiaryDao {

    @Insert
    suspend fun insert(entry: FoodDiary)

    @Query("SELECT * FROM food_diary WHERE userId = :userId ORDER BY diaryDate DESC, diaryTime DESC")
    fun getAllEntries(userId: Int): LiveData<List<FoodDiary>>

    @Query("""
        SELECT SUM(waterAmount) as totalWater, 
               SUM(calories) as totalCalories, 
               SUM(protein) as totalProtein, 
               SUM(fat) as totalFat, 
               SUM(carbs) as totalCarbs 
        FROM food_diary 
        WHERE userId = :userId AND diaryDate = :date
    """)
    suspend fun getDailySummary(userId: Int, date: String): DailySummary?
}

data class DailySummary(
    val totalWater: Float,
    val totalCalories: Float,
    val totalProtein: Float,
    val totalFat: Float,
    val totalCarbs: Float
)