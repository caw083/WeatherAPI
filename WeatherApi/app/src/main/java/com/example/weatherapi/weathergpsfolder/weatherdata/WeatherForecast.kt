package com.example.weatherapi.weathergpsfolder.weatherdata

data class WeatherForecast(
    val dt: Long,
    val main: MainData,
    val weather: List<Weather>,
    val clouds: Clouds,
    val wind: Wind,
    val visibility: Int,
    val pop: Double,
    val rain: Rain,
    val sys: Sys,
    val dt_txt: String
)