package com.example.weathertriggerapp2.network
import android.content.Context
import com.example.weathertriggerapp2.data.WeatherResponse
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Query

private const val BASE_URL = "https://api.openweathermap.org/"

interface OpenWeatherApiService {
    // fixed lat and lon values - practise implementation
    @GET("data/2.5/weather?lat=55.86&lon=-4.25&appid=ed339cdb731796705ce70f8b33f20291")
    fun getWeather() : Call<WeatherResponse>

    // takes in current location of user
    @GET("data/2.5/weather")
    fun getWeatherLocation(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") appid : String
    ): Call<WeatherResponse>
}

// Still to implement caching? Maybe?
object WeatherApi {
    private val retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .build()

    val retrofitService : OpenWeatherApiService by lazy {
        retrofit.create(OpenWeatherApiService::class.java)
    }
}

