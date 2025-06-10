package com.example.nutrition_app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.nutrition_app.model.AppDatabase
import com.example.nutrition_app.model.entities.Recipe
import kotlinx.coroutines.launch

class RecipesViewModel(private val db: AppDatabase) : ViewModel() {
    private val _favoriteRecipes = MutableLiveData<List<Recipe>>()
    val favoriteRecipes: LiveData<List<Recipe>> = _favoriteRecipes

    private val _userRecipes = MutableLiveData<List<Recipe>>()
    val userRecipes: LiveData<List<Recipe>> = _userRecipes

    private val _updateResult = MutableLiveData<Result<Unit>>()
    val updateResult: LiveData<Result<Unit>> = _updateResult

    fun loadRecipes(userId: Int) {
        viewModelScope.launch {
            db.recipeDao().getFavoriteRecipes(userId).collect { favorites ->
                _favoriteRecipes.value = favorites
            }
        }
        viewModelScope.launch {
            db.recipeDao().getUserRecipes(userId).collect { recipes ->
                _userRecipes.value = recipes
            }
        }
    }

    fun toggleFavorite(recipeId: Int) {
        viewModelScope.launch {
            val recipe = db.recipeDao().getRecipeById(recipeId)
            if (recipe != null) {
                val updatedRecipe = recipe.copy(isFavorite = !recipe.isFavorite)
                db.recipeDao().update(updatedRecipe)
            }
        }
    }

    class Factory(private val db: AppDatabase) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(RecipesViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return RecipesViewModel(db) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}