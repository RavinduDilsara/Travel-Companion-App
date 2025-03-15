package com.project.travelcompanionapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.project.travelcompanionapp.repository.MapsRepository

class MapsViewModel(private val repository: MapsRepository) : ViewModel() {

    private val _userLocation = MutableLiveData<LatLng?>()
    val userLocation: LiveData<LatLng?> get() = _userLocation

    private val _nearbyPlaces = MutableLiveData<List<Pair<String, LatLng>>>()
    val nearbyPlaces: LiveData<List<Pair<String, LatLng>>> get() = _nearbyPlaces

    private val _searchLocation = MutableLiveData<LatLng?>()
    val searchLocation: LiveData<LatLng?> get() = _searchLocation

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun fetchUserLocation() {
        repository.getCurrentLocation(
            onResult = { location ->

                _userLocation.postValue(location)
            },
            onError = { errorMessage ->

                Log.e("LocationError", errorMessage)
            }
        )
    }

    fun fetchNearbyPlaces(
            googleMap: GoogleMap,
            type: String,
            placeMarkers: MutableList<Marker>
        ) {
            userLocation.value?.let { location ->
                repository.getNearbyPlaces(
                    googleMap,
                    location,
                    type,
                    placeMarkers,
                    onResult = { places ->
                        _nearbyPlaces.postValue(places)
                    },
                    onError = { error ->
                        _errorMessage.postValue(error)
                    }
                )
            } ?: _errorMessage.postValue("User location not found")
        }


        fun searchForLocation(query: String) {
            repository.searchLocation(query,
                onResult = { location -> _searchLocation.postValue(location) },
                onError = { error -> _errorMessage.postValue(error) }
            )
        }


    }

