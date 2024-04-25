package com.example.weathertriggerapp2.repository

import com.example.weathertriggerapp2.data.Calorie
import com.example.weathertriggerapp2.data.CalorieDao
import com.example.weathertriggerapp2.repository.OfflineCaloriesRepository
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

class OfflineRepoTest {

    @Mock
    private lateinit var mockCDao: CalorieDao

    private lateinit var offlineRepoC: OfflineCaloriesRepository

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        offlineRepoC = OfflineCaloriesRepository(mockCDao)
    }

    @Test
    fun testInsert() = runBlocking {
        val testVal = Calorie(1, "2578.0", "7500.0", "200.0", "80.0", 2);
        offlineRepoC.insert(testVal)
        verify(mockCDao).insert(testVal)
    }

    @Test
    fun testWeeklySteps() {
        runBlocking { offlineRepoC.getStepsCountWeekly(2) }
        verify(mockCDao).getStepsCountWeekly(2)
    }

    @Test
    fun testWeeklyCalories() {
        runBlocking { offlineRepoC.getWeeklyCalorieCount(2) }
        verify(mockCDao).getCaloriesCountWeekly(2)
    }

    @Test
    fun testGetDaysRecorded() {
        runBlocking { offlineRepoC.getDaysRecorded(2) }
        verify(mockCDao).getDaysRecorded(2)
    }

    @Test
    fun testWeeklySugarCount() {
        runBlocking { offlineRepoC.getWeeklySugarCount(2) }
        // Assuming ceDao is a typo and it should be cDao
        verify(mockCDao).getSugarCountWeekly(2)
    }

    @Test
    fun testGetWFC() {
        runBlocking { offlineRepoC.getWeeklyFatCount(2) }
        verify(mockCDao).getFatCountWeekly(2)
    }

    @Test
    fun testDel() {
        runBlocking { offlineRepoC.delete(2) }
        verify(mockCDao).delete(2)
    }
}
