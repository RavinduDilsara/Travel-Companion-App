package com.project.travelcompanionapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.project.travelcompanionapp.model.ItemModel
import com.project.travelcompanionapp.repository.SearchRepository

class SearchViewModel : ViewModel() {

    private val repository = SearchRepository()

    private val searchQuery = MutableLiveData<String>()
    private val _filteredDestinations = MediatorLiveData<List<ItemModel>>()

    val filteredDestinations: LiveData<List<ItemModel>> = _filteredDestinations
    private val destinationList: LiveData<List<ItemModel>> = repository.destinationList

    init {
        _filteredDestinations.apply {
            addSource(repository.destinationList) { updateFilteredDestinations() }
            addSource(searchQuery) { updateFilteredDestinations() }
        }
    }

    fun setDestinations() {
        repository.fetchDestinations()
    }

    fun setSearchQuery(query: String) {
        searchQuery.value = query
    }

    private fun updateFilteredDestinations() {
        val query = searchQuery.value.orEmpty().lowercase()
        val allDestinations = destinationList.value.orEmpty()

        val filteredList = if (query.isEmpty()) {
            emptyList()
        } else {
            allDestinations.filter { it.title.lowercase().contains(query) }
        }

        _filteredDestinations.postValue(filteredList.take(3))
    }
}

