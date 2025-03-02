package com.project.travelcompanionapp.model

data class WeatherApiResponse(
    val name: String,
    val weather: List<WeatherCondition>,
    val main: MainData,
    val wind: WindData,
    val clouds: CloudData,

    )

data class WeatherCondition(
    val description: String,
    val icon: String
)

data class MainData(
    val temp: Double,
    val humidity: Int
)

data class WindData(
    val speed: Double,

    )

data class CloudData(
    val all: Int
)
