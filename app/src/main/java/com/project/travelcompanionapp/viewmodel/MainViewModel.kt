package com.project.travelcompanionapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.*
import com.project.travelcompanionapp.model.DestinationListModel
import com.project.travelcompanionapp.model.ItemModel
import com.project.travelcompanionapp.model.SliderModel

class MainViewModel : ViewModel() {

    private val firebaseDatabase = FirebaseDatabase.getInstance()

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
