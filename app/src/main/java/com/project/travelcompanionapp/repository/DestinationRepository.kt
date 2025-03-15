package com.project.travelcompanionapp.repository

import com.google.firebase.database.*
import com.project.travelcompanionapp.model.ItemModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class DestinationRepository {

    private val firebaseDatabase = FirebaseDatabase.getInstance()
    private val destinationRef = firebaseDatabase.getReference("Destination")

    private val _destinationList = MutableLiveData<List<ItemModel>>()
    val destinationList: LiveData<List<ItemModel>> = _destinationList


    private val _destinationDetails = MutableLiveData<ItemModel>()
    val destinationDetails: LiveData<ItemModel> = _destinationDetails

    fun fetchDestinationList() {
        destinationRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val destinationItems = mutableListOf<ItemModel>()
                for (child in snapshot.children) {
                    child.getValue(ItemModel::class.java)?.let { destinationItems.add(it) }
                }
                _destinationList.value = destinationItems
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun fetchDestinationDetails(title: String) {
        destinationRef.orderByChild("title").equalTo(title).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val destination = snapshot.children.first().getValue(ItemModel::class.java)
                    destination?.let {
                        _destinationDetails.value = it
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}
