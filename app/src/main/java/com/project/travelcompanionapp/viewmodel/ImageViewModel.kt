package com.project.travelcompanionapp.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.project.travelcompanionapp.repository.ImageRepository
import kotlinx.coroutines.launch

class ImageViewModel(application: Application) : AndroidViewModel(application) {

    private val imageRepository = ImageRepository(application)

    private val _imageUri = MutableLiveData<Uri>()
    val imageUri: LiveData<Uri> get() = _imageUri

    private val _imagePath = MutableLiveData<String?>()
    val imagePath: LiveData<String?> get() = _imagePath


    fun saveImageUri(uri: Uri) {
        viewModelScope.launch {
            imageRepository.saveImageUri(uri.toString())
            _imageUri.postValue(uri)
        }
    }


    fun saveImagePath(path: String) {
        viewModelScope.launch {
            imageRepository.saveImagePath(path)
            _imagePath.postValue(path)
        }
    }


    fun loadSavedImage() {
        val savedUri = imageRepository.getSavedImageUri()
        val savedPath = imageRepository.getSavedImagePath()

        if (!savedPath.isNullOrEmpty()) {
            _imagePath.value = savedPath
        } else if (!savedUri.isNullOrEmpty()) {
            _imageUri.value = Uri.parse(savedUri)
        }
    }
}
