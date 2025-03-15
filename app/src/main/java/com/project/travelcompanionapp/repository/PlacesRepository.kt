package com.project.travelcompanionapp.repository

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.project.travelcompanionapp.R
import com.project.travelcompanionapp.model.Place
import com.project.travelcompanionapp.model.PlaceResponse
import java.io.InputStream
import java.io.InputStreamReader

class PlacesRepository(private val context: Context) {


    private val gson = Gson()

    private val inputStream: InputStream
        get() = context.resources.openRawResource(R.raw.places)


    fun read(): List<Place> {
        val itemType = object : TypeToken<List<PlaceResponse>>() {}.type
        val reader = InputStreamReader(inputStream)
        return gson.fromJson<List<PlaceResponse>>(reader, itemType).map {
            it.toPlace()
        }
    }
}