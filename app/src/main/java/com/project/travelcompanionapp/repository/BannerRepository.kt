package com.project.travelcompanionapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.*
import com.project.travelcompanionapp.model.BannerSliderModel

class BannerRepository {

    private val firebaseDatabase = FirebaseDatabase.getInstance()
    private val _banners = MutableLiveData<List<BannerSliderModel>>()
    val banners: LiveData<List<BannerSliderModel>> = _banners

    fun loadBanners() {
        val ref = firebaseDatabase.getReference("Banner")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val bannerList = mutableListOf<BannerSliderModel>()
                for (childSnapshot in snapshot.children) {
                    childSnapshot.getValue(BannerSliderModel::class.java)?.let { bannerList.add(it) }
                }
                _banners.value = bannerList
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}
