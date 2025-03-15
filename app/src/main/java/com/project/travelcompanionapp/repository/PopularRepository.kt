package com.project.travelcompanionapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.*
import com.project.travelcompanionapp.model.ItemModel

class PopularRepository {

    private val firebaseDatabase = FirebaseDatabase.getInstance()
    private val _popular = MutableLiveData<List<ItemModel>>()
    val popular: LiveData<List<ItemModel>> = _popular

    fun loadPopular() {
        val ref = firebaseDatabase.getReference("Popular")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val popularList = mutableListOf<ItemModel>()
                for (childSnapshot in snapshot.children) {
                    childSnapshot.getValue(ItemModel::class.java)?.let { popularList.add(it) }
                }
                _popular.value = popularList
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}
