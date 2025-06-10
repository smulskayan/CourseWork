package com.example.nutrition_app.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.nutrition_app.model.dao.FoodDao
import com.example.nutrition_app.model.dao.FoodDiaryDao
import com.example.nutrition_app.model.dao.RecipeDao
import com.example.nutrition_app.model.dao.UserDao
import com.example.nutrition_app.model.entities.Food
import com.example.nutrition_app.model.entities.FoodDiary
import com.example.nutrition_app.model.entities.Recipe
import com.example.nutrition_app.model.entities.RecipeFood
import com.example.nutrition_app.model.entities.User

@Database(
    entities = [User::class, FoodDiary::class, Recipe::class, Food::class, RecipeFood::class],
    version = 2
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun foodDiaryDao(): FoodDiaryDao
    abstract fun recipeDao(): RecipeDao
    abstract fun foodDao(): FoodDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .fallbackToDestructiveMigration() // Это удалит старую БД при изменении схемы
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}
