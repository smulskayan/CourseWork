package com.example.nutrition_app.model

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE food_diary ADD COLUMN calories REAL NOT NULL DEFAULT 0")
        database.execSQL("ALTER TABLE food_diary ADD COLUMN protein REAL NOT NULL DEFAULT 0")
        database.execSQL("ALTER TABLE food_diary ADD COLUMN fat REAL NOT NULL DEFAULT 0")
        database.execSQL("ALTER TABLE food_diary ADD COLUMN carbs REAL NOT NULL DEFAULT 0")
    }
}