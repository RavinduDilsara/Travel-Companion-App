package com.project.travelcompanionapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.project.travelcompanionapp.model.ItemModel
import com.project.travelcompanionapp.repository.DestinationRepository

class DestinationViewModel : ViewModel() {

    private val repository = DestinationRepository()

    val destinationList: LiveData<List<ItemModel>> = repository.destinationList
    val destinationDetails: LiveData<ItemModel> = repository.destinationDetails



    fun fetchDestinationList() {
        repository.fetchDestinationList()
    }

    fun fetchDestinationDetails(title: String) {
        repository.fetchDestinationDetails(title)
    }
}
