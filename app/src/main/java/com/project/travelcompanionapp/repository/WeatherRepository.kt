package com.project.travelcompanionapp.repository


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.project.travelcompanionapp.BuildConfig
import com.project.travelcompanionapp.network.WeatherService
import com.project.travelcompanionapp.model.ForecastResponse
import com.project.travelcompanionapp.model.WeatherApiResponse
import com.project.travelcompanionapp.util.SingleLiveEvent
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WeatherRepository {

    private val apiKey = BuildConfig.WEATHER_API
    private val weatherService: WeatherService = Retrofit.Builder()
        .baseUrl("https://api.openweathermap.org/data/2.5/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(WeatherService::class.java)

    private val _weatherData = MutableLiveData<WeatherApiResponse>()
    val weatherData: LiveData<WeatherApiResponse> = _weatherData

    private val _forecastData = MutableLiveData<ForecastResponse>()
    val forecastData: LiveData<ForecastResponse> = _forecastData

    private val _errorMessage = SingleLiveEvent<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    suspend fun fetchWeather(city: String) {
        try {
            val weather = weatherService.getWeather(city, apiKey)
            val forecast = weatherService.getForecast(city, apiKey)


            _weatherData.postValue(weather)
            _forecastData.postValue(forecast)
        } catch (e: HttpException) {
            _errorMessage.postValue("HTTP error: ${e.message()}")
        } catch (e: Exception) {
            _errorMessage.postValue("Error: ${e.localizedMessage}")
        }
    }
}
