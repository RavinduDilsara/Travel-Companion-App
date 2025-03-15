package com.project.travelcompanionapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.travelcompanionapp.model.ForecastResponse
import com.project.travelcompanionapp.model.WeatherApiResponse
import com.project.travelcompanionapp.repository.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {

    private val repository = WeatherRepository()

    val weatherData: LiveData<WeatherApiResponse> = repository.weatherData
    val forecastData: LiveData<ForecastResponse> = repository.forecastData
    val errorMessage: LiveData<String> = repository.errorMessage

    fun fetchWeather(city: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.fetchWeather(city)
        }
    }


}
