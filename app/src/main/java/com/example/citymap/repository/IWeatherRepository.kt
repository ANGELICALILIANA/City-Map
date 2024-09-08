package com.example.citymap.repository

import com.example.citymap.vo.LocationVO

interface IWeatherRepository {

    suspend fun getDataWeather()

    suspend fun sendDataWeather(latitude: Double, longitude: Double, name: String): LocationVO?


    suspend fun searchLocationByName(query: String): LocationVO?

}