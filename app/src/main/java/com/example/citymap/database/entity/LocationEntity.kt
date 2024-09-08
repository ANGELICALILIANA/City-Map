package com.example.citymap.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "locations")
data class LocationEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val timezone : String,
    val timezoneOffset : Int,
    var currentTime : Long,
    var sunrise : Long,
    var sunset : Long,
    var temperature : Float,
    var feelsLike : Float,
    var pressure : Int,
    var humidity : Int,
    var dewPoint : Float,
    var uv : Float,
    var clouds : Int,
    var visibility : Int,
    var windSpeed : Float,
    var windDirection : Int,
    var windGust : Float,
    var idWeather : Int,
    var mainWeather : String,
    var descriptionWeather : String,
    var iconWeather : String
)