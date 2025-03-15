package com.project.travelcompanionapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.project.travelcompanionapp.model.Place
import com.project.travelcompanionapp.repository.PlacesRepository

class PlacesViewModel(private val repository: PlacesRepository) : ViewModel() {

    private val _places = MutableLiveData<List<Place>>()
    val places: LiveData<List<Place>> = _places

    fun loadPlaces() {

        _places.value = repository.read()
    }
}
