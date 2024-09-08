package com.example.citymap.dto

import com.google.gson.annotations.SerializedName

data class CoordinateDTO(
    @SerializedName("lat") var latitude: Double,
    @SerializedName("lon") var longitude: Double
)