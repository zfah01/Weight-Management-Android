package com.example.weathertriggerapp2.repository

import com.example.weathertriggerapp2.data.Clouds
import com.example.weathertriggerapp2.data.Coord
import com.example.weathertriggerapp2.data.Main
import com.example.weathertriggerapp2.data.Sys
import com.example.weathertriggerapp2.data.Weather
import com.example.weathertriggerapp2.data.WeatherResponse
import com.example.weathertriggerapp2.data.Wind
import com.example.weathertriggerapp2.network.OpenWeatherApiService
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import retrofit2.Call
import retrofit2.Response

/**
 * Class representing WeatherRepository unit tests
 * */
class WeatherRepoTest {
    private lateinit var weatherRepo: WeatherRepository
    private lateinit var mockWeatherApi: OpenWeatherApiService

    @Before
    fun setup() {
        mockWeatherApi = mock(OpenWeatherApiService::class.java)
        weatherRepo = NetworkWeatherRepository(mockWeatherApi)
    }

    @Test
    fun testGetWeatherLocation() = runBlocking {

        val id = "55"
        val lat = 33.754
        val lon = -134.3456
        val resExpected = WeatherResponse(
            coord = Coord(lon,lat),
            weather = listOf(
                Weather( 1,  "Rain",  "rainy", "06d"),
                Weather( 2, "Sunny",  "bright sky",  "07d")
            ),
            base = "New York",
            main = Main( 11.0,  12.0,  11.0,  28.0,  890, 33),
            visibility = 8,
            wind = Wind( 8.0,  189),
            clouds = Clouds(2),
            dt = 355345,
            sys = Sys( 18977867,  23,  "US",  1273218, 124214),
            timezone = -342,
            id = 2,
            name = "New York",
            cod = 67
        )

        val call = mock(Call::class.java)
        `when`(call.execute()).thenReturn(Response.success(resExpected))
        `when`(mockWeatherApi.getWeatherLocation(lat, lon, id)).thenReturn(call as Call<WeatherResponse>)

        val res = weatherRepo.getWeatherLocation(id, lat, lon)

        assertEquals(resExpected, res)
    }

    @Test
    fun testInvalidParamsGetWeatherLocation() = runBlocking {

        val id = "22"
        val lat = 0.0
        val lon = 0.0

        val resExpected = WeatherResponse(
            coord = Coord( lon,  lat),
            weather = emptyList(),
            base = "",
            main = Main( 0.0,  0.0, 0.0,  0.0,  0,  0),
            visibility = 0,
            wind = Wind( 0.0,  0),
            clouds = Clouds( 0),
            dt = 0,
            sys = Sys( 0, 0,  "",  0,  0),
            timezone = 0,
            id = 0,
            name = "",
            cod = 404
        )

        val call = mock(Call::class.java)
        `when`(call.execute()).thenReturn(Response.success(resExpected))
        `when`(mockWeatherApi.getWeatherLocation(lat, lon, id)).thenReturn(call as Call<WeatherResponse>)

        val res = weatherRepo.getWeatherLocation(id, lat, lon)

        assertEquals(resExpected, res)
    }
}