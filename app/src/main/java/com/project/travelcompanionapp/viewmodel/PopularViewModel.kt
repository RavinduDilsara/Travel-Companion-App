package com.project.travelcompanionapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.project.travelcompanionapp.model.ItemModel
import com.project.travelcompanionapp.repository.PopularRepository

class PopularViewModel : ViewModel() {

    private val repository = PopularRepository()
    val popular: LiveData<List<ItemModel>> = repository.popular

    fun loadPopular() {
        repository.loadPopular()
    }
}
