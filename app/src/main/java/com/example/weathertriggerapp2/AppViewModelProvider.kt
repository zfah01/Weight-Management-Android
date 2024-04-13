package com.example.weathertriggerapp2

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.weathertriggerapp2.viewModel.CalorieViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            CalorieViewModel(
                CalorieApplication().container.calorieRepository
            )
        }
    }
}

fun CreationExtras.CalorieApplication(): CalorieApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as CalorieApplication)
