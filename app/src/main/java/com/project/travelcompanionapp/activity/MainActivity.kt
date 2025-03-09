package com.project.travelcompanionapp.activity


import android.os.Bundle
import com.ismaeldivita.chipnavigation.ChipNavigationBar
import com.project.travelcompanionapp.R
import androidx.fragment.app.Fragment


class MainActivity : BaseActivity(), HomeFragment.HomeFragmentListener {

    private lateinit var bottomNavigation: ChipNavigationBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigation = findViewById(R.id.bottom_navigation)

        bottomNavigation.setOnItemSelectedListener { id ->
            val fragment = when (id) {
                R.id.home -> HomeFragment()
                R.id.maps -> MapsFragment()
                R.id.weather -> WeatherFragment()
                R.id.camera -> CameraFragment()
                else -> HomeFragment()
            }
            loadFragment(fragment)
        }

        if (savedInstanceState == null) {
            bottomNavigation.setItemSelected(R.id.home, true)
            loadFragment(HomeFragment())
        }
    }


    override fun onSeeMoreClicked() {
        loadFragment(WeatherFragment())
        bottomNavigation.setItemSelected(R.id.weather, true)
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
