package com.example.weathertriggerapp2.viewModel

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weathertriggerapp2.AppViewModelProvider
import com.example.weathertriggerapp2.R
import com.example.weathertriggerapp2.data.UserDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.reflect.KFunction2


/**
 * Composable functional representing MainScreen
 * */
@Composable
fun MainScreen(
    applicationContext: Context,
    modifier: Modifier = Modifier,
    calorieViewModel: CalorieViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    InputScreen(/*calorieUiState*/ calorieViewModel :: getCalorieInfo, modifier, applicationContext)
}

/**
 * Composable functional representing InputScreen
 * */
@Composable
fun InputScreen(
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

    /**
     * Function for validating food item field against empty input
     * */
    fun validateEmptyFieldFoodItem(text: String) {
        isErrorFoodItem = text.isEmpty() || text.isNullOrBlank()
    }

    /**
     * Function for validating serving size field against empty input
     * */
    fun validateEmptyFieldServingSize(text: String) {
        isErrorServingSize = text.isEmpty() || text.isNullOrBlank()
    }

    /**
     * Function for validating food item field against sql injection
     * */
    fun validateSQLInjectionItem(text: String) {
        val regexPattern = Regex("^[a-zA-Z0-9' -]+$")
        isSQLPresentItem = !text.matches(regexPattern)
    }

    /**
     * Function for validating serving size field against sql injection
     * */
    fun validateSQLInjectionServingSize(text: String) {
        val regexPattern = Regex("^[0-9]+$")
        isSQLPresentSize = !text.matches(regexPattern)
    }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
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
            label = {
                Text(
                    text = stringResource(id = R.string.food_item),
                    color = Color.Black
                ) },
            isError = isErrorFoodItem,
            supportingText = {
                if (isErrorFoodItem) {
                    Text(
                        text = stringResource(R.string.input_required),
                        color = MaterialTheme.colorScheme.error
                    )
                }
                else if (isSQLPresentItem) {
                    Text(text = stringResource(R.string.sql_present),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
        )
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacer)))

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
            label = {
                Text(
                    text = stringResource(id = R.string.serving_size),
                    color = Color.Black
                )
            },
            isError = isErrorServingSize,
            supportingText = {
                if (isErrorServingSize) {
                    Text(
                        text = stringResource(R.string.input_required),
                        color = MaterialTheme.colorScheme.error
                    )
                }
                else if (isSQLPresentSize) {
                    Text(text = stringResource(R.string.sql_present_numbers),
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
            Text(
                text = stringResource(id = R.string.add),
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacer_add)))

        if(errorMessage) {
            Text(
                text = stringResource(R.string.error_message),
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}


