package com.example.weatherapi.weathergpsfolder.weatherdata

import com.example.weatherapi.weathergpsfolder.weatherdata.WeatherForecast


data class WeatherResponse(
    val cod: String,
    val message: Int,
    val cnt: Int,
    val list: List<WeatherForecast>
)
