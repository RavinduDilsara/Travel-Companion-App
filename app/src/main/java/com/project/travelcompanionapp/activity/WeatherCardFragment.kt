package com.project.travelcompanionapp.activity



import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.project.travelcompanionapp.BuildConfig
import com.project.travelcompanionapp.R
import com.project.travelcompanionapp.WeatherService
import com.project.travelcompanionapp.model.ForecastResponse
import com.project.travelcompanionapp.model.WeatherApiResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Locale

class WeatherCardFragment : Fragment() {

    private val apiKey =  BuildConfig.WEATHER_API
    private lateinit var weatherService: WeatherService

    private lateinit var progressBar: ProgressBar
    private lateinit var txtCity: TextView
    private lateinit var txtTemperature: TextView
    private lateinit var txtWeatherCondition: TextView
    private lateinit var txtWindSpeed: TextView
    private lateinit var txtHumidity: TextView
    private lateinit var txtRainProbability: TextView
    private lateinit var weatherIcon: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_weather_card, container, false)

        // Initialize UI Elements
        progressBar = view.findViewById(R.id.progressBarWeather)
        txtCity = view.findViewById(R.id.textCityName)
        txtTemperature = view.findViewById(R.id.temperature)
        txtWeatherCondition = view.findViewById(R.id.weatherCondition)
        txtWindSpeed = view.findViewById(R.id.textWind)
        txtHumidity = view.findViewById(R.id.textHumidity)
        txtRainProbability = view.findViewById(R.id.textChanceOfRain)
        weatherIcon = view.findViewById(R.id.weatherIcon)

        // Initialize Retrofit
        weatherService = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherService::class.java)

        // Get city name from arguments (default to "Galle")
        val cityName = arguments?.getString("city_name") ?: "Galle"

        getWeatherData(cityName)

        return view
    }

    private fun getWeatherData(city: String) {
        progressBar.visibility = View.VISIBLE

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val weatherData = weatherService.getWeather(city, apiKey)
                val forecastData = weatherService.getForecast(city, apiKey)

                withContext(Dispatchers.Main) {
                    updateUI(weatherData,forecastData)
                    progressBar.visibility = View.GONE
                }
            } catch (e: HttpException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    progressBar.visibility = View.GONE
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Failed to fetch weather", Toast.LENGTH_SHORT).show()
                    progressBar.visibility = View.GONE
                }
            }
        }
    }

    private fun updateUI(weatherData: WeatherApiResponse, forecastData: ForecastResponse) {
        txtCity.text = weatherData.name
        txtTemperature.text = "${weatherData.main.temp.toInt()}°C"
        txtWeatherCondition.text = weatherData.weather[0].description.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(
                Locale.ROOT
            ) else it.toString()
        }
        txtWindSpeed.text = " ${String.format("%d", (weatherData.wind.speed * 3.6).toInt())} km/h"
        txtHumidity.text = " ${weatherData.main.humidity}%"

        val todayPop = forecastData.list[0].pop
        txtRainProbability.text = " ${(todayPop * 100).toInt()}%"

        val iconUrl = "https://openweathermap.org/img/w/${weatherData.weather[0].icon}.png"
        Glide.with(this).load(iconUrl).into(weatherIcon)
    }


}
