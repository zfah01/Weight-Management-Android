package com.example.weathertriggerapp2

import com.example.weathertriggerapp2.data.Calorie
import com.example.weathertriggerapp2.data.CalorieDao
import com.example.weathertriggerapp2.repository.CalorieRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import kotlinx.coroutines.test.TestCoroutineDispatcher

class CalorieRepoTest {

    @Mock
    private lateinit var calorieMockDao: CalorieDao

    private lateinit var calorieRepo: CalorieRepository

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        calorieRepo = CalorieRepository(calorieMockDao)
    }

    @Test
    fun testInsert() = runBlocking {
        val testVal = Calorie(1, "2578.0", "7500.0", "200.0", "80.0", 2);

        calorieRepo.insert(testVal)

        verify(calorieMockDao).insert(testVal)
    }


    @Test
    fun testWeeklySteps() {
        val resultExpected = 50000.0

        `when`(calorieMockDao.getStepsCountWeekly(1)).thenReturn(resultExpected)

        val result = calorieRepo.getStepsCountWeekly(1)
        assertEquals(resultExpected, result, 0.0)
    }

    @Test
    fun testWeeklyCalories() {
        val resultExpected = 12600.0

        `when`(calorieMockDao.getCaloriesCountWeekly(1)).thenReturn(resultExpected)

        val result = calorieRepo.getWeeklyCalorieCount(1)
        assertEquals(resultExpected, result, 0.0)
    }

    @Test
    fun testDaysRecorded() {
        val resultExpected = 4

        `when`(calorieMockDao.getDaysRecorded(1)).thenReturn(resultExpected)

        val result = calorieRepo.getDaysRecorded(1)
        assertEquals(resultExpected, result)
    }

    @Test
    fun testWeeklySugarCount() {
        val resultExpected = 250.0

        `when`(calorieMockDao.getSugarCountWeekly(1)).thenReturn(resultExpected)

        val result = calorieRepo.getWeeklySugarCount(1)
        assertEquals(resultExpected, result, 0.0)
    }

    @Test
    fun testWeeklyFatCount() {
        val resultExpected = 140.0

        `when`(calorieMockDao.getFatCountWeekly(1)).thenReturn(resultExpected)

        val result = calorieRepo.getWeeklyFatCount(1)
        assertEquals(resultExpected, result, 0.0)
    }

    @Test
    fun testDelete() {
        calorieRepo.delete(1)
        verify(calorieMockDao).delete(1)
    }
}