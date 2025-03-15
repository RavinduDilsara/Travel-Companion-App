package com.project.travelcompanionapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.project.travelcompanionapp.view.fragment.HomeFragment
import com.project.travelcompanionapp.view.fragment.MapsFragment
import com.project.travelcompanionapp.view.fragment.WeatherFragment
import com.project.travelcompanionapp.view.fragment.CameraFragment
import androidx.fragment.app.Fragment
import com.project.travelcompanionapp.R

class MainViewModel : ViewModel() {

    private val _selectedFragment = MutableLiveData<Fragment>()
    val selectedFragment: LiveData<Fragment> = _selectedFragment

    init {
        _selectedFragment.value = HomeFragment()
    }

    fun setSelectedFragment(id: Int) {
        val fragment = when (id) {
            R.id.home -> HomeFragment()
            R.id.maps -> MapsFragment()
            R.id.weather -> WeatherFragment()
            R.id.camera -> CameraFragment()
            else -> HomeFragment()
        }
        _selectedFragment.value = fragment
    }

    fun onSeeMoreClicked() {
        _selectedFragment.value = WeatherFragment()
    }
}
