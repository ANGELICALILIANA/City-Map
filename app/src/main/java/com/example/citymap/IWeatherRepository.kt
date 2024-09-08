package com.example.citymap

import com.example.citymap.VO.LocationDatabaseVO

interface IWeatherRepository {

    suspend fun getDataWeather()

    suspend fun sendDataWeather(latitude: Double, longitude: Double)

    suspend fun insertLocation(locationVO: LocationDatabaseVO)

    suspend fun searchLocationByName(query: String): LocationDatabaseVO?

}