package com.example.weathertriggerapp2.network
import com.example.weathertriggerapp2.data.WeatherResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

// https://blog.stackademic.com/cache-me-if-you-can-achieving-caching-with-retrofit-in-kotlin-e77b38a26417
/**
 * Interface representing Nutrition API http calls
 * */
interface OpenWeatherApiService {
    @Headers("Cache-Control: max-age=3600")
    @GET("data/2.5/weather")
    fun getWeatherLocation(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") appid : String
    ): Call<WeatherResponse>
}



