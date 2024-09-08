package com.example.citymap.DTO

import com.google.gson.annotations.SerializedName

data class CoordinateDTO(
    @SerializedName("lat") var latitude: Double,
    @SerializedName("lon") var longitude: Double
)