package com.example.citymap

interface IWeatherRepository {

    suspend fun getDataWeather()

    suspend fun sendDataWeather(latitude: Float, longitude: Float)

}