package com.project.travelcompanionapp.model

data class ForecastResponse(
    val list: List<ForecastItem>
)

data class ForecastItem(
    val dt: Long,
    val main: Main,
    val weather: List<Weather>,
    val pop: Double
)

data class Main(
    val temp: Double
)

data class Weather(
    val description: String,
    val icon: String
)