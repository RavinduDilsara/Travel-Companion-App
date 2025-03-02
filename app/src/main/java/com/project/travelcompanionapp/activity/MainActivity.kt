package com.project.travelcompanionapp.activity

import android.os.Bundle
import com.ismaeldivita.chipnavigation.ChipNavigationBar
import com.project.travelcompanionapp.R


class MainActivity : BaseActivity() {

    private lateinit var bottomNavigation: ChipNavigationBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigation = findViewById(R.id.bottom_navigation)
        bottomNavigation.setOnItemSelectedListener { id ->
            val fragment = when (id) {
                R.id.home -> HomeFragment()
                R.id.maps -> WeatherFragment()
                R.id.weather -> MapsFragment()
                R.id.camera -> MapsFragment()
                else -> HomeFragment()
            }
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
        }


        if (savedInstanceState == null) {
            bottomNavigation.setItemSelected(R.id.home, true)
        }
    }
}
