package com.example.nutrition_app.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.nutrition_app.model.entities.User

@Dao
interface UserDao {
    @Insert
    suspend fun insert(user: User)

    @Update
    suspend fun update(user: User)

    @Query("SELECT * FROM user WHERE email = :email AND password = :password")
    suspend fun getUser(email: String, password: String): User?

    @Query("SELECT * FROM user WHERE email = :email")
    suspend fun getUserByEmail(email: String): User?

    @Query("SELECT * FROM user WHERE id = :id")
    suspend fun getUserById(id: Int): User?
}