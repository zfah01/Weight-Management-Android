package com.example.weathertriggerapp2.viewModel

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weathertriggerapp2.network.NutritionApi
import com.example.weathertriggerapp2.repository.CalorieCountRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Class representing CalorieViewModel
 * */
class CalorieViewModel(@SuppressLint("StaticFieldLeak") val context: Context) : ViewModel() {

    private var totalCalories = 0.0
    private var totalSugar = 0.0
    private var totalSaturatedFat = 0.0


    /**
     * Setter functional for calorie count
     * */
    private fun setCalorieCount(calorieValue: Double) {
        CalorieCountRepository.calorieCount = calorieValue
        Log.i("TAG", "COUNT: " + CalorieCountRepository.calorieCount)
    }

    /**
     * Setter functional for saturated fat count
     * */
    private fun setSaturatedFatIntake(satFatValue: Double) {
        CalorieCountRepository.saturatedFatCount = satFatValue
        Log.i("TAG", "FAT COUNT: " + CalorieCountRepository.saturatedFatCount)
    }

    /**
     * Setter functional for sugar count
     * */
    private fun setSugarIntake(sugarValue: Double) {
        CalorieCountRepository.sugarCount = sugarValue
        Log.i("TAG", "SUGAR COUNT: " + CalorieCountRepository.sugarCount)
    }

    /**
     * Function for reset viewmodel variable values
     * */
    fun resetViewModel() {
        totalCalories = 0.0
        totalSugar = 0.0
        totalSaturatedFat = 0.0
        setCalorieCount(0.0)
        setSugarIntake(0.0)
        setSaturatedFatIntake(0.0)
        Log.i("TAG", "COUNT: " + CalorieCountRepository.calorieCount)
        Log.i("TAG", "COUNT: $totalCalories")
    }

    /**
     * Function to receive information associated with inputted food item and serving size
     * @param foodItem - inputted food item
     * @param servingSize - inputted serving size in grams (g)
     * */
    fun getCalorieInfo(foodItem: String, servingSize : String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val query = servingSize + "g " + foodItem
                val response = NutritionApi(context).nutritionRepository.getNutritionInfo(query, "f7In2PE7kPlaQSKXU+WR6g==KpmVcVM9KH707liC")

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