package com.example.weathertriggerapp2.viewModel

import android.content.Context
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.runtime.Composable
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weathertriggerapp2.AppViewModelProvider
import com.example.weathertriggerapp2.data.NutritionResponse
import com.example.weathertriggerapp2.data.UserDataStore
import com.example.weathertriggerapp2.repository.CalorieCountRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Timer
import kotlin.concurrent.schedule
import kotlin.reflect.KFunction2



@Composable
fun MainScreen(
    applicationContext: Context,
    modifier: Modifier = Modifier,
    calorieViewModel: CalorieViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val calorieUiState = calorieViewModel.foodUiState
    InputScreen(calorieUiState, calorieViewModel :: getCalorieInfo, modifier, applicationContext)
}

@Composable
fun InputScreen(
    calorieUiState: List<NutritionResponse>?,
    getCalorieInfo: KFunction2<String, String, Unit>,
    modifier: Modifier = Modifier,
    applicationContext: Context) {
    var foodItem by remember { mutableStateOf("") }
    var servingSize by remember { mutableStateOf("") }
    var isErrorFoodItem by rememberSaveable { mutableStateOf(false) }
    var isErrorServingSize by rememberSaveable { mutableStateOf(false) }
    var errorMessage by rememberSaveable { mutableStateOf(false) }
    var isSQLPresentItem by rememberSaveable { mutableStateOf(false) }
    var isSQLPresentSize by rememberSaveable { mutableStateOf(false) }
    val radioOptions = listOf("Male", "Female")
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[1]) }

    val store = UserDataStore(applicationContext)

    var textReturn = ""

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
        Text(
            text = "% of Calories Consumed",
            fontSize = 20.sp,
            modifier = Modifier.padding(top = 10.dp)
        )
        var progress by remember { mutableFloatStateOf(0.0f) }
        val animatedProgress by animateFloatAsState(
            targetValue = progress,
            animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec, label = ""
        )
        CircularProgressIndicator(
            progress = animatedProgress,
            color = if(progress<=1){Color.Green} else {Color.Red},
            modifier = Modifier.size(200.dp),
            strokeWidth = 30.dp
        )
        radioOptions.forEach { text ->
            Row(
                Modifier
                    .selectable(
                        selected = (text == selectedOption),
                        onClick = { onOptionSelected(text) }
                    )
            ) {
                RadioButton(
                    selected = (text == selectedOption),
                    onClick = {
                        onOptionSelected(text)
                        CoroutineScope(Dispatchers.IO).launch { store.saveToken(text) }
                    }
                )
                Text(
                    text = text
                )
            }
        }
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
                        text = "Input required",
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
                        text = "Input required",
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

                //reference for adding delay - https://www.tutorialspoint.com/how-to-call-a-function-after-a-delay-in-kotlin
                Timer().schedule(1000){
                    progress = calcPercentage(selectedOption);
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

fun calcPercentage(selectedOption: String): Float {
    var percentageConsumed = 0.00f
    percentageConsumed = if(selectedOption == "Male"){
        (CalorieCountRepository.calorieCount?.div(2000))?.toFloat() ?: 0.00f
    }
    else{
        (CalorieCountRepository.calorieCount?.div(1500))?.toFloat() ?: 0.00f
    }
    return percentageConsumed
}


