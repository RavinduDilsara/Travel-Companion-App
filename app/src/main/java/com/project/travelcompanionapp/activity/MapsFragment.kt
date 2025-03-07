package com.project.travelcompanionapp.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions
import com.project.travelcompanionapp.R
import com.project.travelcompanionapp.model.Place

class MapsFragment : Fragment(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private val places: List<Place> by lazy {
        PlacesReader(requireContext()).read() // Ensure this is implemented
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        addMarkers(googleMap)
    }

    private fun addMarkers(googleMap: GoogleMap) {
        places.forEach { place ->
            place.latLng?.let { latLng ->
                googleMap.addMarker(
                    MarkerOptions()
                        .title(place.name)
                        .position(latLng)
                )
            }
        }

        if (places.isNotEmpty()) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(places.first().latLng, 10f))
        }
    }
}
