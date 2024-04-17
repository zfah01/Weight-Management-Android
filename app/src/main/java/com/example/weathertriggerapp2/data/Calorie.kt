package com.example.weathertriggerapp2.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "calories")
data class Calorie(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo(name = "calorie_count")
    val calorieCount: String?,
    @ColumnInfo(name = "step_count")
    val stepCount: Double,
    @ColumnInfo(name = "most_common_food_type")
    val mostCommonFoodType: String,
    @ColumnInfo(name = "number_of_week")
    val weekNum: Int
)