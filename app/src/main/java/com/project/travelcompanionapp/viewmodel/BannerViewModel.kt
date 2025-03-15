package com.project.travelcompanionapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.project.travelcompanionapp.model.BannerSliderModel
import com.project.travelcompanionapp.repository.BannerRepository

class BannerViewModel : ViewModel() {

    private val repository = BannerRepository()
    val banners: LiveData<List<BannerSliderModel>> = repository.banners

    fun loadBanners() {
        repository.loadBanners()
    }
}
