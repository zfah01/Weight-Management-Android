package com.example.weathertriggerapp2.repository

import com.example.weathertriggerapp2.repository.CalorieCountRepository
import org.junit.Assert.assertEquals
import org.junit.Test

class CalorieCountRepoTest {

    @Test
    fun testInitialize() {

        assertEquals(0.0, CalorieCountRepository.calorieCount)
        assertEquals(0.0, CalorieCountRepository.sugarCount)
        assertEquals(0.0, CalorieCountRepository.saturatedFatCount)
        assertEquals(0, CalorieCountRepository.stepCount)
        assertEquals(0, CalorieCountRepository.goalSteps)
    }

    @Test
    fun testAssignVal() {

        CalorieCountRepository.calorieCount = 2500.0
        CalorieCountRepository.sugarCount = 33.3
        CalorieCountRepository.saturatedFatCount = 50.0
        CalorieCountRepository.stepCount = 3000
        CalorieCountRepository.goalSteps = 6000

        assertEquals(2500.0,  CalorieCountRepository.calorieCount)
        assertEquals(33.3, CalorieCountRepository.sugarCount)
        assertEquals(50.0, CalorieCountRepository.saturatedFatCount)
        assertEquals(3000, CalorieCountRepository.stepCount)
        assertEquals(6000, CalorieCountRepository.goalSteps)
    }
}
