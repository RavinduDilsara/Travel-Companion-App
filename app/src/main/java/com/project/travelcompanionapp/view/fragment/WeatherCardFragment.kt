package com.project.travelcompanionapp.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.project.travelcompanionapp.R
import com.project.travelcompanionapp.model.ForecastResponse
import com.project.travelcompanionapp.model.WeatherApiResponse
import com.project.travelcompanionapp.viewmodel.WeatherViewModel
import java.util.Locale
import kotlin.math.roundToInt

class WeatherCardFragment : Fragment() {

    private lateinit var viewModel: WeatherViewModel

    private lateinit var weatherContainer: ViewGroup
    private lateinit var txtCity: TextView
    private lateinit var txtTemperature: TextView
    private lateinit var txtWeatherCondition: TextView
    private lateinit var txtWindSpeed: TextView
    private lateinit var txtHumidity: TextView
    private lateinit var txtPrecipitation: TextView
    private lateinit var weatherIcon: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_weather_card, container, false)


        viewModel = ViewModelProvider(requireActivity())[WeatherViewModel::class.java]

        weatherContainer = view.findViewById(R.id.cardViewWeather)
        txtCity = view.findViewById(R.id.textCityName)
        txtTemperature = view.findViewById(R.id.temperature)
        txtWeatherCondition = view.findViewById(R.id.weatherCondition)
        txtWindSpeed = view.findViewById(R.id.textWind)
        txtHumidity = view.findViewById(R.id.textHumidity)
        txtPrecipitation = view.findViewById(R.id.textPrecipitation)
        weatherIcon = view.findViewById(R.id.weatherIcon)

        val cityName = arguments?.getString("city_name") ?: "Galle"


        viewModel.fetchWeather(cityName)

        viewModel.weatherData.observe(viewLifecycleOwner) { weatherData ->
            weatherData?.let { updateWeatherUI(it) }
        }


        viewModel.forecastData.observe(viewLifecycleOwner) { forecastData ->
            forecastData?.let { updateRainProbability(it) }
        }


        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()

        }

        return view
    }

      private fun updateWeatherUI(weatherData: WeatherApiResponse) {
        txtCity.text = weatherData.name
        txtTemperature.text = "${weatherData.main.temp.roundToInt()}°C"
        txtWeatherCondition.text = weatherData.weather[0].description.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
        }
        txtWindSpeed.text = " ${String.format("%d", (weatherData.wind.speed * 3.6).roundToInt())} km/h"
        txtHumidity.text = " ${weatherData.main.humidity}%"

        val iconUrl = "https://openweathermap.org/img/w/${weatherData.weather[0].icon}.png"
        Glide.with(this).load(iconUrl).into(weatherIcon)
    }

    private fun updateRainProbability(forecastData: ForecastResponse) {
        val todayPop = forecastData.list[0].pop
        txtPrecipitation.text = " ${(todayPop * 100).roundToInt()}%"
    }
}
