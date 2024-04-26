package com.example.weathertriggerapp2.viewModel


import android.content.Context
import com.example.weathertriggerapp2.repository.CalorieCountRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mock
import org.mockito.Mockito.mock

class ViewModelTests() {

    @Mock
    val viewModel = CalorieViewModel(mock(Context::class.java))

    @OptIn(ExperimentalCoroutinesApi::class)
    @BeforeEach
    fun setUp() {
        // Set the main dispatcher for testing
        Dispatchers.setMain(Dispatchers.Default)
    }


    // test user input with invalid food
    @Test
    fun testFoodNotString(){
        var originalCalories = viewModel.totalCalories
        var originalFats = viewModel.totalSaturatedFat
        var originalSugar = viewModel.totalSugar
        var foodItem = "35"
        var servingSize = "30"
        viewModel.getCalorieInfo(foodItem, servingSize)

        assertTrue(originalCalories.equals(viewModel.totalCalories))
        assertTrue(originalFats.equals(viewModel.totalSaturatedFat))
        assertTrue(originalSugar.equals(viewModel.totalSugar))
    }

    //test user input with invalid portion
    @Test
    fun testServingNotDouble(){
        var originalCalories = viewModel.totalCalories
        var originalFats = viewModel.totalSaturatedFat
        var originalSugar = viewModel.totalSugar
        var foodItem = "35"
        foodItem.toDoubleOrNull()
        var servingSize = "30"
        viewModel.getCalorieInfo(foodItem, servingSize)
        if(foodItem != null){
            assertTrue(originalCalories.equals(viewModel.totalCalories))
            assertTrue(originalFats.equals(viewModel.totalSaturatedFat))
            assertTrue(originalSugar.equals(viewModel.totalSugar))
        }
    }
    //test if fields are empty
    @Test
    fun testIfEmpty(){
        var originalCalories = viewModel.totalCalories
        var originalFats = viewModel.totalSaturatedFat
        var originalSugar = viewModel.totalSugar
        var foodItem = ""
        var servingSize = ""
        viewModel.getCalorieInfo(foodItem, servingSize)
        assertTrue(originalCalories.equals(viewModel.totalCalories))
        assertTrue(originalFats.equals(viewModel.totalSaturatedFat))
        assertTrue(originalSugar.equals(viewModel.totalSugar))
    }

    //Check food item length is greater than 2
    @Test
    fun testFoodItemLength(){
        var originalCalories = viewModel.totalCalories
        var originalFats = viewModel.totalSaturatedFat
        var originalSugar = viewModel.totalSugar
        var foodItem = ""
        var servingSize = "30"
        viewModel.getCalorieInfo(foodItem, servingSize)
        if(foodItem.length <= 2){
            assertTrue(originalCalories.equals(viewModel.totalCalories))
            assertTrue(originalFats.equals(viewModel.totalSaturatedFat))
            assertTrue(originalSugar.equals(viewModel.totalSugar))
        }
    }

    //test set fat
    @Test
    fun testSetFat(){
        viewModel.setSaturatedFatIntake(35.0)
        assertEquals(35.0, CalorieCountRepository.saturatedFatCount)
    }

    //test set sugar
    @Test
    fun testSetSugar(){
        viewModel.setSugarIntake(56.0)
        assertEquals(56.0, CalorieCountRepository.sugarCount)
    }
    //test set calories
    @Test
    fun testSetCalories(){
        viewModel.setCalorieCount(255.5)
        assertEquals(255.5, CalorieCountRepository.calorieCount)
    }

    //test reset all
    @Test
    fun testResetModel(){
        viewModel.resetViewModel()

        assertEquals(0.0, CalorieCountRepository.calorieCount)
        assertEquals(0.0, CalorieCountRepository.sugarCount)
        assertEquals(0.0, CalorieCountRepository.saturatedFatCount)
    }
}