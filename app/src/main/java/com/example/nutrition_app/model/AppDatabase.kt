package com.example.nutrition_app.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.nutrition_app.model.dao.FoodDiaryDao
import com.example.nutrition_app.model.dao.RecipeDao
import com.example.nutrition_app.model.dao.UserDao
import com.example.nutrition_app.model.entities.FoodDiary
import com.example.nutrition_app.model.entities.Recipe
import com.example.nutrition_app.model.entities.User

@Database(
    entities = [User::class, FoodDiary::class, Recipe::class],
    version = 3
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun foodDiaryDao(): FoodDiaryDao
    abstract fun recipeDao(): RecipeDao

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
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}
