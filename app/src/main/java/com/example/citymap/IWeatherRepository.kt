package com.example.citymap

import com.example.citymap.VO.LocationVO

interface IWeatherRepository {

    suspend fun getDataWeather()

    suspend fun sendDataWeather(latitude: Double, longitude: Double, name: String): LocationVO?


    suspend fun searchLocationByName(query: String): LocationVO?

}