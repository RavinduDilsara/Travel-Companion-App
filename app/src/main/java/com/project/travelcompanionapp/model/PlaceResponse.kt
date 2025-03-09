package com.project.travelcompanionapp.model

import com.google.android.gms.maps.model.LatLng

data class PlaceResponse(
    val geometry: Geometry,
    val name: String,

) {
    fun toPlace(): Place {
        return Place(
            name = this.name,
            latLng = LatLng(this.geometry.location.lat, this.geometry.location.lng),
            )
    }
}
data class Place(
    val name: String,
    val latLng: LatLng,


)

data class Geometry(
    val location: Location
)
data class Location(
    val lat: Double,
    val lng: Double
)