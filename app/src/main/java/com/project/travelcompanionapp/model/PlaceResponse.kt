package com.project.travelcompanionapp.model

import com.google.android.gms.maps.model.LatLng

data class PlaceResponse(
    val geometry: Geometry,
    val name: String,
    val vicinity: String,
    val rating: Float
) {
    fun toPlace(): Place {
        return Place(
            name = this.name,
            latLng = LatLng(this.geometry.location.lat, this.geometry.location.lng),
            address = this.vicinity,
            rating = this.rating
        )
    }
}
data class Place(
    val name: String,
    val latLng: LatLng,
    val address: String,
    val rating: Float
)

data class Geometry(
    val location: Location
)
data class Location(
    val lat: Double,
    val lng: Double
)

