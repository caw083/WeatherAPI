package com.example.weatherapi.weathergpsfolder

import com.example.weatherapi.weathergpsfolder.weatherdata.WeatherResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


// Define Retrofit related components
interface WeatherApi {
    @GET("forecast")
    fun getWeatherForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String
    ): Call<WeatherResponse>
}