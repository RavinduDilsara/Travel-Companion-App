package com.project.travelcompanionapp.model

import java.io.Serializable

data class ItemModel(
    var title: String = "",
    var address: String = "",
    var description: String = "",
    var images: List<String> = emptyList(),
    var score: Double = 0.0,
    var isPopular: Boolean = false
) : Serializable