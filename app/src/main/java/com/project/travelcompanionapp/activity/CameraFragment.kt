package com.project.travelcompanionapp.activity

import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.project.travelcompanionapp.R
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*

class CameraFragment : Fragment() {

    private lateinit var imageView: ImageView
    private lateinit var sharedPreferences: SharedPreferences
    private var imageUri: Uri? = null


    private val takePicture = registerForActivityResult(ActivityResultContracts.TakePicture()) { success: Boolean ->
        if (success) {
            imageUri?.let {
                imageView.setImageURI(it)
                saveImageUri(it.toString())
            }
        }
    }


    private val selectImageFromGallery = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            val savedFilePath = saveImageToInternalStorage(uri)
            if (savedFilePath != null) {
                imageView.setImageURI(Uri.parse(savedFilePath))
                saveImagePath(savedFilePath)
                loadSavedImage()
            } else {
                Toast.makeText(requireContext(), "Failed to save image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_camera, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = requireContext().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)

        imageView = view.findViewById(R.id.imageView)
        val buttonGallery = view.findViewById<Button>(R.id.galleryBtn)
        val buttonCamera = view.findViewById<Button>(R.id.cameraBtn)


        buttonCamera.setOnClickListener {
            imageUri = createImageUri()
            imageUri?.let { uri ->
                takePicture.launch(uri)
            } ?: run {
                Toast.makeText(requireContext(), "Failed to create image Uri", Toast.LENGTH_SHORT).show()
            }
        }


        buttonGallery.setOnClickListener {
            selectImageFromGallery.launch("image/*")
        }


        loadSavedImage()
    }


    private fun createImageUri(): Uri? {
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, generateFileName())
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraApp")
        }
        return requireContext().contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
    }


    private fun generateFileName(): String {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        return "IMG_$timeStamp.jpg"
    }


    private fun saveImageToInternalStorage(uri: Uri): String? {
        return try {
            val inputStream: InputStream = requireContext().contentResolver.openInputStream(uri) ?: return null
            val fileName = "selected_image.jpg"
            val file = File(requireContext().filesDir, fileName)
            val outputStream = FileOutputStream(file)

            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()

            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    private fun saveImageUri(uri: String) {
        sharedPreferences.edit().putString("saved_image_uri", uri).apply()
    }


    private fun saveImagePath(filePath: String) {
        sharedPreferences.edit().putString("saved_image_path", filePath).apply()
    }


    private fun loadSavedImage() {
        val savedFilePath = sharedPreferences.getString("saved_image_path", null)
        val savedUri = sharedPreferences.getString("saved_image_uri", null)

        if (!savedFilePath.isNullOrEmpty()) {
            val imageFile = File(savedFilePath)
            if (imageFile.exists()) {
                imageView.setImageURI(Uri.fromFile(imageFile))
                return
            }
        }

        if (!savedUri.isNullOrEmpty()) {
            imageView.setImageURI(Uri.parse(savedUri))
        }
    }
}
