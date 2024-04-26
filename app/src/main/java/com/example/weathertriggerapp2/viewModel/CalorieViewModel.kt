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

    var totalCalories = 0.0
    var totalSugar = 0.0
    var totalSaturatedFat = 0.0

//    /**
//     * Upon initialization, check to see if current time is midnight
//     * */
//    init {
//        viewModelScope.launch {
//            resetViewModelVariables()
//        }
//    }
//
//    /**
//     * Function to check if current time is midnight. If so, reset all variables to zero
//     * */
//    private suspend fun resetViewModelVariables() {
//        while (true) {
//            val cal = Calendar.getInstance()
//            cal.timeInMillis = System.currentTimeMillis()
//            cal.set(Calendar.HOUR_OF_DAY, 0)
//            cal.set(Calendar.MINUTE, 0)
//            cal.set(Calendar.SECOND, 0)
//            cal.set(Calendar.MILLISECOND, 0)
//            val timeAtCurrent = System.currentTimeMillis()
//            val timeAtMidnight = cal.timeInMillis
//            var time = timeAtMidnight - timeAtCurrent
//            if (time < 0) {
//                cal.add(Calendar.DAY_OF_MONTH, 1)
//                time = cal.timeInMillis - timeAtCurrent
//            }
//            Log.i(TAG, "resetting variables delayed until midnight")
//
//            delay(time)
//
//            Log.i(TAG, "currently midnight time to reset variables")
//
//            resetViewModel()
//        }
//    }


    /**
     * Setter function for calorie count
     * */
    fun setCalorieCount(calorieValue: Double) {
        CalorieCountRepository.calorieCount = calorieValue
    }

    /**
     * Setter function for saturated fat count
     * */
    fun setSaturatedFatIntake(satFatValue: Double) {
        CalorieCountRepository.saturatedFatCount = satFatValue
    }

    /**
     * Setter function for sugar count
     * */
    fun setSugarIntake(sugarValue: Double) {
        CalorieCountRepository.sugarCount = sugarValue
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