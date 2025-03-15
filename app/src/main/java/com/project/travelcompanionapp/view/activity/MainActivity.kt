package com.project.travelcompanionapp.view.activity

import android.os.Bundle
import androidx.activity.viewModels
import com.ismaeldivita.chipnavigation.ChipNavigationBar
import com.project.travelcompanionapp.R
import com.project.travelcompanionapp.databinding.ActivityMainBinding
import com.project.travelcompanionapp.viewmodel.MainViewModel
import androidx.fragment.app.Fragment
import com.project.travelcompanionapp.view.fragment.HomeFragment

class MainActivity : BaseActivity(), HomeFragment.HomeFragmentListener {

    private lateinit var bottomNavigation: ChipNavigationBar
    private lateinit var binding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bottomNavigation = findViewById(R.id.bottom_navigation)


        mainViewModel.selectedFragment.observe(this) { fragment ->
            loadFragment(fragment)
        }

        bottomNavigation.setOnItemSelectedListener { id ->
            mainViewModel.setSelectedFragment(id)
        }

        if (savedInstanceState == null) {
            bottomNavigation.setItemSelected(R.id.home, true)
            loadFragment(HomeFragment())
        }
    }

    override fun onSeeMoreClicked() {
        mainViewModel.onSeeMoreClicked()
        bottomNavigation.setItemSelected(R.id.weather, true)
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
