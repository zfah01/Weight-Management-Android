package com.example.weathertriggerapp2.viewModel

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weathertriggerapp2.data.Calorie
import com.example.weathertriggerapp2.data.NutritionResponse
import com.example.weathertriggerapp2.network.NutritionApi
import com.example.weathertriggerapp2.repository.CalorieRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

class CalorieViewModel(private val calorieRepository: CalorieRepository) : ViewModel() {

    var totalCalories = 0.0
    var foodUiState by mutableStateOf<List<NutritionResponse>?>(null)
        private set

    fun getCalorieInfo(foodItem: String, servingSize : String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val query = servingSize + "g " + foodItem
                // Execute the network request
                val response: Response<List<NutritionResponse>> = NutritionApi.retrofitService.getNutritionInfo(
                    "f7In2PE7kPlaQSKXU+WR6g==KpmVcVM9KH707liC", query)
                    .execute()

                // Check if the response is successful
                if (response.isSuccessful) {
                    Log.i(TAG, "Successful!")
                    val responseList: List<NutritionResponse>? = response.body()
                    foodUiState = responseList

                    for((j, _) in response.body()!!.withIndex()){
                        Log.i(TAG, "TOTAL: " + (response.body()?.get(j)?.calories))
                        totalCalories += (response.body()?.get(j)?.calories) ?: 0.0
                    }
                    Log.i(TAG, "RESULT: $totalCalories")

//                    val newCalorie = Calorie( 0, totalCalories, 0.0, "")
//                    calorieRepository.insert(newCalorie)
                } else {
                    Log.e(TAG, "Error occurred")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error occurred: ${e.message}", e)
            }
        }
    }
}