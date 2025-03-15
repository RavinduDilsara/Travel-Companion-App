package com.project.travelcompanionapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.project.travelcompanionapp.model.ItemModel

class SearchRepository {

    private val database = FirebaseDatabase.getInstance()
    private val _destinationList = MutableLiveData<List<ItemModel>>()
    val destinationList: LiveData<List<ItemModel>> = _destinationList


    fun fetchDestinations() {
        val ref = database.getReference("Destination")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val destinations = mutableListOf<ItemModel>()
                for (childSnapshot in snapshot.children) {
                    val destination = childSnapshot.getValue(ItemModel::class.java)
                    destination?.let {
                        destinations.add(it)
                    }
                }
                _destinationList.value = destinations
            }

            override fun onCancelled(error: DatabaseError) {

                _destinationList.value = emptyList()
            }
        })
    }

}
