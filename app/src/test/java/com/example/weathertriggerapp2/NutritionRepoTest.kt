package com.example.weathertriggerapp2

import com.example.weathertriggerapp2.data.NutritionResponse
import com.example.weathertriggerapp2.network.NutritionApiService
import com.example.weathertriggerapp2.repository.NetworkNutritionRepository
import com.example.weathertriggerapp2.repository.NutritionRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class NutritionRepoTest {

    @Mock
    private lateinit var nutritionServiceMockApi: NutritionApiService

    private lateinit var nutritionRepo: NutritionRepository

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        nutritionRepo = NetworkNutritionRepository(nutritionServiceMockApi)
    }

//    @Test
//    suspend fun testGetNutritionInfo() {
//
//        val nutritionResponseList = listOf(
//            NutritionResponse(
//                "test1", 230.0, 30.0, 12.0, 8.0, 20.5, 70.0,
//                50.5, 33.3, 45.5, 83.0, 12.0
//            ),
//            NutritionResponse(
//                "test2", 122.0, 24.0, 8.2, 22.0, 42.0, 34.0,
//                66.4, 43.4, 64.0, 54.0, 17.7
//            )
//        )
//
//        `when`(nutritionServiceMockApi.getNutritionInfo(anyString(), anyString())).thenReturn(nutritionResponseList)
//
//
//        val result = runBlocking { nutritionRepo.getNutritionInfo("query", "header") }
//
//        assertEquals(nutritionResponseList, result)
//    }

    @Test(expected = Exception::class)
    fun testErrorHandlingGetNutritionInfo() {

        `when`(nutritionServiceMockApi.getNutritionInfo(anyString(), anyString())).thenThrow(Exception("Error"))

        runBlocking { nutritionRepo.getNutritionInfo("query", "header") }
    }
}
