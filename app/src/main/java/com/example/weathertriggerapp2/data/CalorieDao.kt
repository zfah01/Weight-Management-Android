package com.example.weathertriggerapp2.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CalorieDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(calorie: Calorie)
    @Delete
    fun delete(calorie: Calorie)
    @Query("SELECT * FROM calories")
    fun getAll(): Flow<List<Calorie>>
}