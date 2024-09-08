package com.example.citymap.Repository

import com.example.citymap.database.AppDatabase
import com.example.citymap.IApiClient
import com.example.citymap.IWeatherRepository
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val apiClient : IApiClient,
    private val dataBase : AppDatabase,
): IWeatherRepository{

    override suspend fun getDataWeather() {
        TODO("Not yet implemented")
    }

    override suspend fun sendDataWeather(latitude: Double, longitude: Double) {
        //apiClient.getData(latitude, longitude)
    }

}