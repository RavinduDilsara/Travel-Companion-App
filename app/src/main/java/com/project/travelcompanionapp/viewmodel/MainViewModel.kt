package com.project.travelcompanionapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.*
import com.project.travelcompanionapp.BuildConfig
import com.project.travelcompanionapp.WeatherService
import com.project.travelcompanionapp.model.DestinationListModel
import com.project.travelcompanionapp.model.ForecastResponse
import com.project.travelcompanionapp.model.ItemModel
import com.project.travelcompanionapp.model.SliderModel
import com.project.travelcompanionapp.model.WeatherApiResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainViewModel : ViewModel() {

    private val firebaseDatabase = FirebaseDatabase.getInstance()

    private val apiKey = BuildConfig.WEATHER_API
    private val weatherService: WeatherService = Retrofit.Builder()
        .baseUrl("https://api.openweathermap.org/data/2.5/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(WeatherService::class.java)

    private val _weatherData = MutableLiveData<WeatherApiResponse>()
    val weatherData: LiveData<WeatherApiResponse?> = _weatherData

    private val _forecastData = MutableLiveData<ForecastResponse>()
    val forecastData: LiveData<ForecastResponse?> = _forecastData

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val _banners = MutableLiveData<List<SliderModel>>()
    private val _popular = MutableLiveData<List<ItemModel>>()
    private val _destination = MutableLiveData<List<DestinationListModel>>()

    val searchQuery = MutableLiveData<String>()

    val banners: LiveData<List<SliderModel>> = _banners
    val popular: LiveData<List<ItemModel>> = _popular


    val filteredDestinations = MediatorLiveData<List<DestinationListModel>>().apply {
        addSource(_destination) { updateFilteredDestinations() }
        addSource(searchQuery) { updateFilteredDestinations() }
    }

    fun fetchWeather(city: String) {
        viewModelScope.launch(Dispatchers.IO) {
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

    private fun updateFilteredDestinations() {
        val query = searchQuery.value.orEmpty()
        val allDestinations = _destination.value.orEmpty()

        val filteredList = if (query.isEmpty()) {
            allDestinations
        } else {
            allDestinations.filter { it.title.contains(query, ignoreCase = true) }
        }


        filteredDestinations.postValue(filteredList.take(3))
    }



    fun loadPopular() {
        fetchData("Popular", _popular, ItemModel::class.java)
    }

    fun loadDestination() {
        fetchData("Popular", _destination, DestinationListModel::class.java)
    }

    fun loadBanners() {
        fetchData("Banner", _banners, SliderModel::class.java)
    }

    private fun <T> fetchData(reference: String, liveData: MutableLiveData<List<T>>, modelClass: Class<T>) {
        val ref = firebaseDatabase.getReference(reference)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lists = mutableListOf<T>()
                for (childSnapshot in snapshot.children) {
                    childSnapshot.getValue(modelClass)?.let { lists.add(it) }
                }
                liveData.value = lists
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}
