package com.project.travelcompanionapp.view.fragment

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.project.travelcompanionapp.R
import com.project.travelcompanionapp.model.Place
import com.project.travelcompanionapp.repository.MapsRepository
import com.project.travelcompanionapp.repository.PlacesRepository
import com.project.travelcompanionapp.viewmodel.MapsViewModel
import com.project.travelcompanionapp.viewmodel.PlacesViewModel

class MapsFragment : Fragment(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private lateinit var spinner: Spinner
    private lateinit var searchView: SearchView
    private var searchMarker: Marker? = null
    private lateinit var mapsViewModel: MapsViewModel
    private lateinit var placesViewModel: PlacesViewModel
    private val placeMarkers = mutableListOf<Marker>()
    private var places: List<Place> = emptyList()
    private var currentLocationMarker: Marker? = null


    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            mapsViewModel.fetchUserLocation()
        } else {
            Toast.makeText(requireContext(), "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


         placesViewModel = PlacesViewModel(PlacesRepository(requireContext()))
        placesViewModel.loadPlaces()

        mapsViewModel = MapsViewModel(MapsRepository(requireContext()))

        searchView = view.findViewById(R.id.search_view)
        spinner = view.findViewById(R.id.type_spinner)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        checkLocationPermission()
        setupSearch()
        setupSpinner()
        observeViewModel()
    }

    private fun setupSearch() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    mapsViewModel.searchForLocation(it)

                    val inputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(searchView.windowToken, 0)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }

    private fun setupSpinner() {
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(
                parentView: AdapterView<*>, view: View?, position: Int, id: Long
            ) {
                val selectedType = parentView.getItemAtPosition(position).toString()
                if (selectedType != "- Select -") {

                    mapsViewModel.fetchNearbyPlaces(googleMap, selectedType, placeMarkers)
                }
            }


            override fun onNothingSelected(parentView: AdapterView<*>) {}
        }
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mapsViewModel.fetchUserLocation()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }


    private fun observeViewModel() {

        placesViewModel.places.observe(viewLifecycleOwner) { placesList ->
            places = placesList
        }

        mapsViewModel.searchLocation.observe(viewLifecycleOwner) { location ->
            location?.let { updateMap(it) }
        }


        mapsViewModel.userLocation.observe(viewLifecycleOwner) { location ->
            location?.let {
                val userLatLng = LatLng(it.latitude, it.longitude)


                currentLocationMarker?.remove()


                currentLocationMarker = googleMap.addMarker(
                    MarkerOptions()
                        .position(userLatLng)
                        .title("Your Location")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                )

                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 12f))
            }
        }


        mapsViewModel.nearbyPlaces.observe(viewLifecycleOwner) { places ->

            placeMarkers.forEach { it.remove() }
            placeMarkers.clear()


            places.forEach { (name, latLng) ->
                val marker = googleMap.addMarker(MarkerOptions().position(latLng).title(name))
                marker?.let { placeMarkers.add(it) }
            }
        }


        mapsViewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateMap(location: LatLng) {
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 12f))
        searchMarker?.remove()
        searchMarker = googleMap.addMarker(
            MarkerOptions().position(location).title("Searched Location")
        )
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        addMarkers(googleMap)
        googleMap.uiSettings.isZoomControlsEnabled = true
    }


    private fun addMarkers(googleMap: GoogleMap) {
        places.forEach { place ->
            place.latLng.let { latLng ->
                googleMap.addMarker(
                    MarkerOptions()
                        .title(place.name)
                        .position(latLng)
                )
            }
        }
    }

}