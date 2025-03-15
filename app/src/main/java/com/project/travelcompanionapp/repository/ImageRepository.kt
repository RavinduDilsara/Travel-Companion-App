package com.project.travelcompanionapp.repository

import android.content.Context
import android.content.SharedPreferences

class ImageRepository(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)


    fun saveImageUri(uri: String) {
        sharedPreferences.edit().putString("saved_image_uri", uri).apply()
    }


    fun saveImagePath(filePath: String) {
        sharedPreferences.edit().putString("saved_image_path", filePath).apply()
    }


    fun getSavedImageUri(): String? {
        return sharedPreferences.getString("saved_image_uri", null)
    }


    fun getSavedImagePath(): String? {
        return sharedPreferences.getString("saved_image_path", null)
    }
}
