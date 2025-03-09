package com.project.travelcompanionapp.activity

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.*
import com.project.travelcompanionapp.BuildConfig
import com.project.travelcompanionapp.R


class MapsFragment : Fragment(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var placesClient: PlacesClient
    private var userLocation: LatLng? = null
    private val placeMarkers = mutableListOf<com.google.android.gms.maps.model.Marker>()

    private val places: List<com.project.travelcompanionapp.model.Place> by lazy {
        PlacesReader(requireContext()).read()
    }


    private lateinit var placeTypeSpinner: Spinner
    private lateinit var searchView: SearchView

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            getCurrentLocation()
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

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map_fragment) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        Places.initialize(requireContext(), BuildConfig.PLACES_API)
        placesClient = Places.createClient(requireContext())

        placeTypeSpinner = view.findViewById(R.id.type_spinner)
        searchView = view.findViewById(R.id.search_view)

        placeTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedType = parentView.getItemAtPosition(position).toString()
                if (selectedType != "- Select -") {
                    getNearbyPlaces(selectedType)
                }
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {}
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { searchLocation(it) }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            getCurrentLocation()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        addMarkers(googleMap)
        googleMap.uiSettings.isZoomControlsEnabled = true
    }

    private fun getCurrentLocation() {
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    userLocation = LatLng(it.latitude, it.longitude)

                    val markerOptions = MarkerOptions()
                        .position(userLocation!!)
                        .title("Your Location")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))

                    googleMap.addMarker(markerOptions)
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation!!, 12f))


                } ?: run {
                    Toast.makeText(
                        requireContext(),
                        "Unable to get current location",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } catch (e: SecurityException) {
            Toast.makeText(requireContext(), "Location permission not granted", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun getNearbyPlaces(type: String) {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            return
        }

        if (userLocation == null) {
            Toast.makeText(requireContext(), "User location not found", Toast.LENGTH_SHORT).show()
            return
        }


        placeMarkers.forEach { it.remove() }
        placeMarkers.clear()

        val request = FindCurrentPlaceRequest.newInstance(
            listOf(Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.TYPES)
        )

        placesClient.findCurrentPlace(request)
            .addOnSuccessListener { response ->
                var count = 0
                val userLocationObj = Location("userLocation").apply {
                    latitude = userLocation!!.latitude
                    longitude = userLocation!!.longitude
                }

                for (placeLikelihood in response.placeLikelihoods) {
                    val place = placeLikelihood.place
                    val latLng = place.latLng
                    if (latLng != null && place.types != null) {
                        val placeLocation = Location("placeLocation").apply {
                            latitude = latLng.latitude
                            longitude = latLng.longitude
                        }

                        val distance = userLocationObj.distanceTo(placeLocation)

                        if (distance <= 2000 && place.types!!.any {
                                it.name.contains(
                                    type,
                                    ignoreCase = true
                                )
                            }) {
                            val marker = googleMap.addMarker(
                                MarkerOptions().position(latLng).title(place.name)
                            )
                            marker?.let { placeMarkers.add(it) }
                            count++
                        }
                    }

                    if (count >= 5) break
                }

                if (count > 0) {
                    Toast.makeText(
                        requireContext(),
                        "Showing nearby $type recommendations",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(requireContext(), "No nearby $type found", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to get nearby places", Toast.LENGTH_SHORT)
                    .show()
            }
    }


    private fun searchLocation(query: String) {
        val request = FindAutocompletePredictionsRequest.builder()
            .setQuery(query)
            .setTypeFilter(TypeFilter.GEOCODE)
            .setSessionToken(AutocompleteSessionToken.newInstance())
            .build()

        placesClient.findAutocompletePredictions(request)
            .addOnSuccessListener { response ->
                if (response.autocompletePredictions.isNotEmpty()) {
                    val firstPrediction = response.autocompletePredictions[0]
                    val placeId = firstPrediction.placeId

                    val placeRequest =
                        FetchPlaceRequest.newInstance(placeId, listOf(Place.Field.LAT_LNG))
                    placesClient.fetchPlace(placeRequest).addOnSuccessListener { placeResponse ->
                        val latLng = placeResponse.place.latLng
                        if (latLng != null) {
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                            googleMap.addMarker(MarkerOptions().position(latLng).title(query))
                        }
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Location not found", Toast.LENGTH_SHORT).show()
            }
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
