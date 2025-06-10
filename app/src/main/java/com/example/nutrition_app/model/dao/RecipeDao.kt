package com.example.nutrition_app.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.nutrition_app.model.entities.Recipe
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {
    @Insert
    suspend fun insert(recipe: Recipe)

    @Update
    suspend fun update(recipe: Recipe)

    @Query("SELECT * FROM recipes WHERE userId = :userId AND isFavorite = 1")
    fun getFavoriteRecipes(userId: Int): Flow<List<Recipe>>

    @Query("SELECT * FROM recipes WHERE userId = :userId")
    fun getUserRecipes(userId: Int): Flow<List<Recipe>>

    @Query("SELECT * FROM recipes WHERE id = :recipeId")
    suspend fun getRecipeById(recipeId: Int): Recipe?
}