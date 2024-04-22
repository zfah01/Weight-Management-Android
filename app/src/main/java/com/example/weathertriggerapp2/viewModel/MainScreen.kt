package com.example.weathertriggerapp2.viewModel

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weathertriggerapp2.AppViewModelProvider
import com.example.weathertriggerapp2.data.NutritionResponse
import kotlin.reflect.KFunction2

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    calorieViewModel: CalorieViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val calorieUiState = calorieViewModel.foodUiState
    InputScreen(calorieUiState, calorieViewModel :: getCalorieInfo, modifier)
}

@Composable
fun InputScreen(
    calorieUiState: List<NutritionResponse>?,
    getCalorieInfo: KFunction2<String, String, Unit>,
    modifier: Modifier = Modifier) {
    var foodItem by remember { mutableStateOf("") }
    var servingSize by remember { mutableStateOf("") }
    var isErrorFoodItem by rememberSaveable { mutableStateOf(false) }
    var isErrorServingSize by rememberSaveable { mutableStateOf(false) }
    var errorMessage by rememberSaveable { mutableStateOf(false) }
    var isSQLPresentItem by rememberSaveable { mutableStateOf(false) }
    var isSQLPresentSize by rememberSaveable { mutableStateOf(false) }

    fun validateEmptyFieldFoodItem(text: String) {
        isErrorFoodItem = text.isEmpty() || text.isNullOrBlank()
    }

    fun validateEmptyFieldServingSize(text: String) {
        isErrorServingSize = text.isEmpty() || text.isNullOrBlank()
    }

    fun validateSQLInjectionItem(text: String) {
        val regexPattern = Regex("^[a-zA-Z0-9' -]+$")
        isSQLPresentItem = !text.matches(regexPattern)
    }

    fun validateSQLInjectionServingSize(text: String) {
        val regexPattern = Regex("^[0-9]+$")
        isSQLPresentSize = !text.matches(regexPattern)
    }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        OutlinedTextField(
            value = foodItem,
            onValueChange = {
                foodItem = it
                validateEmptyFieldFoodItem(foodItem)
                validateSQLInjectionItem(foodItem)

            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text
            ),
            label = { Text("Enter food item: ") },
            isError = isErrorFoodItem,
            supportingText = {
                if (isErrorFoodItem) {
                    Text(
                        text = "Input Required",
                        color = MaterialTheme.colorScheme.error
                    )
                }
                else if (isSQLPresentItem) {
                    Text(text = "Only use alphanumeric character and spaces",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
        )
        Spacer(modifier = Modifier.height(5.dp))
        OutlinedTextField(
            value = servingSize,
            onValueChange = {
                servingSize = it
                validateEmptyFieldServingSize(servingSize)
                validateSQLInjectionServingSize(servingSize)
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            label = { Text("Enter serving size in grams (g): ") },
            isError = isErrorServingSize,
            supportingText = {
                if (isErrorServingSize) {
                    Text(
                        text = "Input Required",
                        color = MaterialTheme.colorScheme.error
                    )
                }
                else if (isSQLPresentSize) {
                    Text(text = "Only use numeric characters",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
        )
        Button(
            onClick = {
                if(!isErrorFoodItem && !isErrorServingSize && foodItem != "" && servingSize != "" && !isSQLPresentItem && !isSQLPresentSize) {
                    errorMessage = false
                    getCalorieInfo(foodItem, servingSize)
                    foodItem = ""
                    servingSize = ""
                }
                else {
                    errorMessage = true
                }
            }
        ) {
            Text("Add")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if(errorMessage) {
            Text(
                text = "Input in both text fields are required",
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}


