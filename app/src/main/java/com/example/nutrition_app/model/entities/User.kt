package com.example.nutrition_app.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val firstName: String,
    val gender: String,
    val age: Int,
    val height: Float,
    val weight: Float,
    val email: String,
    val password: String,
    val goal: String,
)