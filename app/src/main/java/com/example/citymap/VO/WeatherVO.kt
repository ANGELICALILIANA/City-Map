package com.example.citymap.VO

import com.example.citymap.DTO.CurrentDTO
import com.example.citymap.DTO.WeatherDTO

data class LocationVO(
    var latitude : Float = 0F,
    var longitude : Float = 0F,
    var timezone : String = "",
    var timezoneOffset : Int = 0,
    var current : CurrentDTO? = null
)

data class CurrentVO(
    var currentTime : Long = 0,
    var sunrise : Long = 0,
    var sunset : Long = 0,
    var temperature : Float = 0F,
    var feelsLike : Float = 0F,
    var pressure : Int = 0,
    var humidity : Int = 0,
    var dewPoint : Float = 0F,
    var uv : Float = 0F,
    var clouds : Int = 0,
    var visibility : Int = 0,
    var windSpeed : Float = 0F,
    var windDirection : Int = 0,
    var windGust : Float = 0F,
    var weather : List<WeatherDTO>? = null
)

data class WeatherVO(
    var idWeather : Int = 0,
    var mainWeather : String = "",
    var descriptionWeather : String = "",
    var iconWeather : String = ""
)

data class LatLongVO(
    var latitude : Double = 0.0,
    var longitude : Double = 0.0
)