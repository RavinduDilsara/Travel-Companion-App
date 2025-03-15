package com.project.travelcompanionapp.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.travelcompanionapp.R
import com.project.travelcompanionapp.view.adapter.WeatherForecastAdapter
import com.project.travelcompanionapp.model.ForecastItem
import com.project.travelcompanionapp.model.ForecastResponse
import com.project.travelcompanionapp.viewmodel.WeatherViewModel
import java.text.SimpleDateFormat
import java.util.*

class WeatherFragment : Fragment() {


    private lateinit var viewModel: WeatherViewModel
    private lateinit var etCity: EditText
    private lateinit var btnSearch: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var forecastAdapter: WeatherForecastAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_weather, container, false)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[WeatherViewModel::class.java]

        etCity = view.findViewById(R.id.etCity)
        btnSearch = view.findViewById(R.id.btnSearch)
        recyclerView = view.findViewById(R.id.recyclerViewForecast)



        forecastAdapter = WeatherForecastAdapter(emptyList())
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = forecastAdapter



        viewModel.forecastData.observe(viewLifecycleOwner) { forecastData ->
            if (forecastData != null) {
                updateForecastUI(forecastData)
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
        }


        btnSearch.setOnClickListener {
            val cityName = etCity.text.toString().trim()
            if (cityName.isNotEmpty()) {

                viewModel.fetchWeather(cityName)
                hideKeyboard()
                etCity.clearFocus()
            } else {
                Toast.makeText(requireContext(), "Please enter a city name", Toast.LENGTH_SHORT).show()
            }

        }

        viewModel.fetchWeather("Galle")
    }


    private fun updateForecastUI(forecastData: ForecastResponse) {
        val dailyForecasts = mutableListOf<ForecastItem>()
        val seenDates = mutableSetOf<String>()
        for (forecast in forecastData.list) {
            val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(forecast.dt * 1000))
            if (seenDates.add(date)) {
                dailyForecasts.add(forecast)
            }
            if (dailyForecasts.size == 5) break
        }


        forecastAdapter.updateData(dailyForecasts)

    }

    private fun hideKeyboard() {
        val inputMethodManager = requireContext().getSystemService(InputMethodManager::class.java)
        val view = requireActivity().currentFocus
        view?.let {
            inputMethodManager?.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}
