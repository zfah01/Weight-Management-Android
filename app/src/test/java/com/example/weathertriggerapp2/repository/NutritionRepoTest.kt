package com.example.weathertriggerapp2.repository


import com.example.weathertriggerapp2.data.NutritionResponse
import com.example.weathertriggerapp2.network.NutritionApiService
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import retrofit2.Call
import retrofit2.Response

/**
 * Class representing NutritionRepository unit tests
 * */
class NutritionRepoTest {
    private lateinit var nutritionRepo: NutritionRepository
    private lateinit var nutritionMockApi: NutritionApiService

    @Before
    fun setup() {
        nutritionMockApi = mock(NutritionApiService::class.java)
        nutritionRepo = NetworkNutritionRepository(nutritionMockApi)
    }

    @Test
    fun testGetNutritionInfo() = runBlocking {

        val query = "test"
        val header = "test"
        val resExpected = listOf(
            NutritionResponse(
                name = "test",
                calories = 234.0,
                serving_size_g = 44.2,
                fat_total_g = 3.4,
                fat_saturated_g = 5.3,
                protein_g = 10.3,
                sodium_mg = 22.0,
                potassium_mg = 44.0,
                cholesterol_mg = 8.0,
                carbohydrates_total_g = 54.2,
                fibre_g = 22.2,
                sugar_g = 20.5
            ),
            NutritionResponse(
                name = "test",
                calories = 333.0,
                serving_size_g = 32.0,
                fat_total_g = 21.2,
                fat_saturated_g = 27.0,
                protein_g = 44.0,
                sodium_mg = 20.0,
                potassium_mg = 89.0,
                cholesterol_mg  = 54.0,
                carbohydrates_total_g = 23.0,
                fibre_g = 22.5,
                sugar_g = 12.0
            )
        )

        val call = mock(Call::class.java)
        `when`(call.execute()).thenReturn(Response.success(resExpected))
        `when`(nutritionMockApi.getNutritionInfo(header, query)).thenReturn(call as Call<List<NutritionResponse>>)

        val res = nutritionRepo.getNutritionInfo(query, header)

        assertEquals(resExpected, res)
    }

    @Test
    fun testInvalidParamsGetNutritionInfo() = runBlocking {

        val query = ""
        val header = ""
        val resExpected = emptyList<NutritionResponse>()

        val call = mock(Call::class.java)
        `when`(call.execute()).thenReturn(Response.success(resExpected))
        `when`(nutritionMockApi.getNutritionInfo(header, query)).thenReturn(call as Call<List<NutritionResponse>>)

        val res = nutritionRepo.getNutritionInfo(query, header)
        assertEquals(resExpected, res)
    }
}