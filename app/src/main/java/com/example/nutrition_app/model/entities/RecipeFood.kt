package com.example.nutrition_app.model.entities

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(tableName = "recipe_foods", primaryKeys = ["recipeId", "foodId"], foreignKeys = [
    ForeignKey(entity = Recipe::class, parentColumns = ["id"], childColumns = ["recipeId"]),
    ForeignKey(entity = Food::class, parentColumns = ["id"], childColumns = ["foodId"])
])
data class RecipeFood(
    val recipeId: Int,
    val foodId: Int,
    val quantity: Float
)