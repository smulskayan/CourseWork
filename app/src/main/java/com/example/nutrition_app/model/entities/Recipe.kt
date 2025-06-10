package com.example.nutrition_app.model.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "recipes", foreignKeys = [
    ForeignKey(entity = User::class, parentColumns = ["id"], childColumns = ["userId"])
])
data class Recipe(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val title: String,
    val photo: String?,
    val carbs: Float,
    val calories: Float,
    val protein: Float,
    val fat: Float,
    val instructions: String,
    val isFavorite: Boolean
)