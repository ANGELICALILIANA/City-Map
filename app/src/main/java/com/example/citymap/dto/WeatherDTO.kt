package com.example.citymap.dto

import com.google.gson.annotations.SerializedName

data class LocationDTO(
    @SerializedName("lat") var latitude : Double,
    @SerializedName("lon") var longitude : Double,
    @SerializedName("timezone") var timezone : String,
    @SerializedName("timezone_offset") var timezoneOffset : Int,
    @SerializedName("current") var current : CurrentDTO
)

data class CurrentDTO(
    @SerializedName("dt") var currentTime : Long,
    @SerializedName("sunrise") var sunrise : Long,
    @SerializedName("sunset") var sunset : Long,
    @SerializedName("temp") var temperature : Float,
    @SerializedName("feels_like") var feelsLike : Float,
    @SerializedName("pressure") var pressure : Int,
    @SerializedName("humidity") var humidity : Int,
    @SerializedName("dew_point") var dewPoint : Float,
    @SerializedName("uvi") var uv : Float,
    @SerializedName("clouds") var clouds : Int,
    @SerializedName("visibility") var visibility : Int,
    @SerializedName("wind_speed") var windSpeed : Float,
    @SerializedName("wind_deg") var windDirection : Int,
    @SerializedName("wind_gust") var windGust : Float,
    @SerializedName("weather") var weather : List<WeatherDTO>
)

data class WeatherDTO(
    @SerializedName("id") var idWeather : Int,
    @SerializedName("main") var mainWeather : String,
    @SerializedName("description") var descriptionWeather : String,
    @SerializedName("icon") var iconWeather : String
)