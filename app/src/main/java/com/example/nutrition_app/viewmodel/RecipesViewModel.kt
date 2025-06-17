package com.example.nutrition_app.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.nutrition_app.model.AppDatabase
import com.example.nutrition_app.model.entities.Recipe
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class RecipesViewModel(private val db: AppDatabase) : ViewModel() {
    private val _allRecipes = MutableLiveData<List<Recipe>>()
    val allRecipes: LiveData<List<Recipe>> = _allRecipes

    private val _showFavoriteTitle = MutableLiveData<Boolean>()
    val showFavoriteTitle: LiveData<Boolean> = _showFavoriteTitle

    private val _navigationEvent = MutableLiveData<NavigationEvent>()
    val navigationEvent: LiveData<NavigationEvent> = _navigationEvent

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private var userId: Int = -1

    sealed class NavigationEvent {
        object NavigateToRecipeEdit : NavigationEvent()
    }

    fun loadUserId(context: Context) {
        viewModelScope.launch {
            userId = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
                .getInt("userId", -1)
            if (userId != -1) {
                db.recipeDao().getUserRecipes(userId)
                    .map { recipes ->
                        recipes.sortedWith(compareByDescending<Recipe> { it.isFavorite }.thenBy { it.title })
                    }
                    .catch { e ->
                        _error.value = "Ошибка загрузки рецептов: ${e.message}"
                    }
                    .collect { sortedRecipes ->
                        _allRecipes.value = sortedRecipes
                        _showFavoriteTitle.value = sortedRecipes.any { it.isFavorite }
                    }
            } else {
                _error.value = "Пользователь не найден"
            }
        }
    }

    fun toggleFavorite(recipeId: Int) {
        viewModelScope.launch {
            try {
                val recipe = db.recipeDao().getRecipeById(recipeId)
                if (recipe != null) {
                    val updatedRecipe = recipe.copy(isFavorite = !recipe.isFavorite)
                    db.recipeDao().update(updatedRecipe)
                }
            } catch (e: Exception) {
                _error.value = "Ошибка обновления избранного: ${e.message}"
            }
        }
    }

    fun onAddRecipeClicked() {
        _navigationEvent.value = NavigationEvent.NavigateToRecipeEdit
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