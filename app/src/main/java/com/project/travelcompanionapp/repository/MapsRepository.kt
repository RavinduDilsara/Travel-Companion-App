package com.project.travelcompanionapp.repository

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.*
import com.google.android.libraries.places.api.net.*
import com.project.travelcompanionapp.BuildConfig

class MapsRepository(private val context: Context) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private val placesClient: PlacesClient

    init {
        if (!Places.isInitialized()) {
            Places.initialize(context, BuildConfig.PLACES_API)
        }
        placesClient = Places.createClient(context)
    }

    fun getCurrentLocation(onResult: (LatLng?) -> Unit, onError: (String) -> Unit) {

        if (ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            onError("Location permission not granted")
            return
        }


        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                val latLng = LatLng(location.latitude, location.longitude)
                onResult(latLng)
            } else {
                onError("Failed to get location")
            }
        }.addOnFailureListener {
            onError("Failed to get location")
        }
    }


    fun getNearbyPlaces(
        googleMap: GoogleMap,
        userLocation: LatLng?,
        type: String,
        placeMarkers: MutableList<Marker>,
        onResult: (List<Pair<String, LatLng>>) -> Unit,
        onError: (String) -> Unit
    ) {
        if (ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            onError("Location permission not granted")
            return
        }

        if (userLocation == null) {
            onError("User location not found")
            return
        }


        placeMarkers.forEach { it.remove() }
        placeMarkers.clear()

        val request = FindCurrentPlaceRequest.newInstance(
            listOf(Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.TYPES)
        )

        placesClient.findCurrentPlace(request)
            .addOnSuccessListener { response ->
                val nearbyPlaces = mutableListOf<Pair<String, LatLng>>()
                val userLocationObj = Location("userLocation").apply {
                    latitude = userLocation.latitude
                    longitude = userLocation.longitude
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
                                it.name.contains(type, ignoreCase = true)
                            }) {
                            val marker = googleMap.addMarker(
                                MarkerOptions().position(latLng).title(place.name)
                            )
                            marker?.let { placeMarkers.add(it) }


                            nearbyPlaces.add(Pair(place.name ?: "Unknown Place", latLng))
                        }

                        if (nearbyPlaces.size >= 5) break
                    }
                }

                if (nearbyPlaces.isNotEmpty()) {
                    onResult(nearbyPlaces)

                    val message = if (nearbyPlaces.size == 1) {
                        "Showing 1 nearby $type recommendation"
                    } else {
                        "Showing ${nearbyPlaces.size} nearby $type recommendations"
                    }

                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                } else {
                    onError("No nearby $type found")
                }

            }
            .addOnFailureListener {
                onError("Failed to get nearby places")
            }
    }

    fun searchLocation(
        query: String,
        onResult: (LatLng?) -> Unit,
        onError: (String) -> Unit
    ) {
        val request = FindAutocompletePredictionsRequest.builder()
            .setQuery(query)
            .setTypeFilter(TypeFilter.GEOCODE)
            .setSessionToken(AutocompleteSessionToken.newInstance())
            .build()

        placesClient.findAutocompletePredictions(request)
            .addOnSuccessListener { response ->
                if (response.autocompletePredictions.isNotEmpty()) {
                    val firstPrediction = response.autocompletePredictions[0]
                    val placeRequest = FetchPlaceRequest.newInstance(
                        firstPrediction.placeId,
                        listOf(Place.Field.LAT_LNG)
                    )

                    placesClient.fetchPlace(placeRequest).addOnSuccessListener { placeResponse ->
                        onResult(placeResponse.place.latLng)
                    }.addOnFailureListener {
                        onError("Location not found")
                    }
                } else {
                    onError("No results found")
                }
            }
            .addOnFailureListener {
                onError("Search request failed")
            }
    }
}