package com.example.weathertriggerapp2.viewModel

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weathertriggerapp2.broadcast.Notification
import com.example.weathertriggerapp2.data.Calorie
import com.example.weathertriggerapp2.data.NutritionResponse
import com.example.weathertriggerapp2.network.NutritionApi
import com.example.weathertriggerapp2.repository.CalorieCountRepository
import com.example.weathertriggerapp2.repository.CalorieRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date

class CalorieViewModel(@SuppressLint("StaticFieldLeak") val context: Context) : ViewModel() {

    var totalCalories = 0.0
    var totalSugar = 0.0
    var totalSaturatedFat = 0.0

    var foodUiState by mutableStateOf<List<NutritionResponse>?>(null)
        set

    fun setCalorieCount(newValue: Double) {
        CalorieCountRepository.calorieCount = newValue
//        Log.i("TAG", "COUNT: " + CalorieCountRepository.calorieCount)
    }

    fun setSaturatedFatIntake(newValue: Double) {
        CalorieCountRepository.saturatedFatCount = newValue
//        Log.i("TAG", "FAT COUNT: " + CalorieCountRepository.saturatedFatCount)
    }

    fun setSugarIntake(newValue: Double) {
        CalorieCountRepository.sugarCount = newValue
//        Log.i("TAG", "SUGAR COUNT: " + CalorieCountRepository.sugarCount)
    }

    fun resetViewModel() {
        totalCalories = 0.0
        totalSugar = 0.0
        totalSaturatedFat = 0.0
        setCalorieCount(0.0)
        setSugarIntake(0.0)
        setSaturatedFatIntake(0.0)
        //Log.i("TAG", "COUNT: " + CalorieCountRepository.calorieCount)
        //Log.i("TAG", "COUNT: $totalCalories")
    }

    fun getCalorieInfo(foodItem: String, servingSize : String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val query = servingSize + "g " + foodItem
                val response = NutritionApi(context).nutritionRepository.getNutritionInfo(query, "f7In2PE7kPlaQSKXU+WR6g==KpmVcVM9KH707liC")
                foodUiState = response

                for((j, _) in response.withIndex()){
                    Log.i(TAG, "TOTAL BEFORE: $totalCalories")
                    Log.i(TAG, "TOTAL: " + (response[j].calories))
                    totalCalories += (response[j].calories)
                    totalSugar += (response[j].sugar_g)
                    totalSaturatedFat += (response[j].fat_saturated_g)
                }
                Log.i(TAG, "RESULT: $totalCalories")
                setCalorieCount(totalCalories)
                setSugarIntake(totalSugar)
                setSaturatedFatIntake(totalSaturatedFat)
            } catch (e: Exception) {
                Log.e(TAG, "Error occurred: ${e.message}", e)
            }
        }
    }
}