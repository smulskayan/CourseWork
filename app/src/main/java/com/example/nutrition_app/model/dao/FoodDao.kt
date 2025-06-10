package com.example.nutrition_app.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.nutrition_app.model.entities.Food

@Dao
interface FoodDao {
    @Insert
    suspend fun insert(food: Food)

    @Query("SELECT * FROM food")
    suspend fun getAllFoods(): List<Food>
}