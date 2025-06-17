package com.example.nutrition_app.viewmodel

import android.content.Context
import androidx.lifecycle.*
import com.example.nutrition_app.model.AppDatabase
import com.example.nutrition_app.model.entities.Recipe
import kotlinx.coroutines.launch

class RecipeEditViewModel(private val db: AppDatabase) : ViewModel() {
    private val _recipe = MutableLiveData<Recipe?>()
    val recipe: LiveData<Recipe?> = _recipe

    private val _saveResult = MutableLiveData<Result<Unit>>()
    val saveResult: LiveData<Result<Unit>> = _saveResult

    fun loadRecipe(recipeId: Int) {
        if (recipeId != 0) {
            viewModelScope.launch {
                _recipe.value = db.recipeDao().getRecipeById(recipeId)
            }
        }
    }

    fun saveRecipe(
        context: Context,
        recipeId: Int,
        title: String,
        photoUri: String?,
        calories: String,
        protein: String,
        fat: String,
        carbs: String,
        instructions: String,
        isFavorite: Boolean
    ) {
        viewModelScope.launch {
            val userId = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
                .getInt("userId", -1)
            if (userId == -1) {
                _saveResult.value = Result.failure(Exception("Пользователь не авторизован"))
                return@launch
            }

            if (title.isBlank()) {
                _saveResult.value = Result.failure(Exception("Название не может быть пустым"))
                return@launch
            }

            val caloriesFloat = calories.toFloatOrNull()
            val proteinFloat = protein.toFloatOrNull()
            val fatFloat = fat.toFloatOrNull()
            val carbsFloat = carbs.toFloatOrNull()

            if (listOf(caloriesFloat, proteinFloat, fatFloat, carbsFloat).any { it == null || it <= 0f }) {
                _saveResult.value = Result.failure(Exception("Пищевая ценность должна быть положительным числом"))
                return@launch
            }

            if (instructions.isBlank()) {
                _saveResult.value = Result.failure(Exception("Инструкция не может быть пустой"))
                return@launch
            }

            val recipe = Recipe(
                id = recipeId,
                userId = userId,
                title = title,
                photo = photoUri,
                calories = caloriesFloat!!,
                protein = proteinFloat!!,
                fat = fatFloat!!,
                carbs = carbsFloat!!,
                instructions = instructions,
                isFavorite = isFavorite
            )

            if (recipeId == 0) {
                db.recipeDao().insert(recipe)
            } else {
                db.recipeDao().update(recipe)
            }
            _saveResult.value = Result.success(Unit)
        }
    }

    class Factory(private val db: AppDatabase) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(RecipeEditViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return RecipeEditViewModel(db) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
