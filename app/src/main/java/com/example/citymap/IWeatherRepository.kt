package com.example.citymap

import com.example.citymap.VO.LocationVO
import com.example.citymap.VO.LocationDatabaseVO

interface IWeatherRepository {

    suspend fun getDataWeather()

    suspend fun sendDataWeather(latitude: Double, longitude: Double): LocationVO?

    suspend fun insertLocation(locationVO: LocationDatabaseVO)

    suspend fun searchLocationByName(query: String): LocationDatabaseVO?

}