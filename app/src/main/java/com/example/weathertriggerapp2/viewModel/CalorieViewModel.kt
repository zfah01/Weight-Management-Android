package com.example.weathertriggerapp2.viewModel

import android.content.ContentValues.TAG
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

class CalorieViewModel(private val calorieRepository: CalorieRepository) : ViewModel() {

    var totalCalories = 0.0
    var foodUiState by mutableStateOf<List<NutritionResponse>?>(null)
        private set

    fun setCalorieCount(newValue: Double) {
        CalorieCountRepository.calorieCount = newValue
        Log.i("TAG", "COUNT: " + CalorieCountRepository.calorieCount)
    }

    fun getCalorieInfo(foodItem: String, servingSize : String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val query = servingSize + "g " + foodItem
                val response = NutritionApi.nutritionRepository.getNutritionInfo(query, "f7In2PE7kPlaQSKXU+WR6g==KpmVcVM9KH707liC")
                foodUiState = response

                for((j, _) in response.withIndex()){
                    Log.i(TAG, "TOTAL: " + (response[j].calories))
                    totalCalories += (response[j].calories)
                }
                Log.i(TAG, "RESULT: $totalCalories")
                setCalorieCount(totalCalories)


                // MOVE TO WORKER CLASS AND INSERT AT SPECIFIC TIME
//                val newCalorie = Calorie(0, totalCalories, 0.0, "")
//                calorieRepository.insert(newCalorie)
            } catch (e: Exception) {
                Log.e(TAG, "Error occurred: ${e.message}", e)
            }
        }
    }
}