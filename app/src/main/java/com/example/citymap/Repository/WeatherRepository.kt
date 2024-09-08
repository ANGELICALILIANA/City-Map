package com.example.citymap.Repository

import com.example.citymap.DTO.WeatherDTO
import com.example.citymap.IApiClient
import com.example.citymap.IWeatherRepository
import com.example.citymap.Mapper
import com.example.citymap.VO.LocationVO
import com.example.citymap.VO.WeatherVO
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val apiClient : IApiClient,
): IWeatherRepository{

    override suspend fun getDataWeather() {
        TODO("Not yet implemented")
    }

    override suspend fun sendDataWeather(latitude: Double, longitude: Double): LocationVO? {
        val response = apiClient.getData(latitude, longitude)
        if (response.isSuccessful) {
            if (response.body() != null) {
                return Mapper().locationVO(response.body()!!)
            } else {
                return LocationVO()
            }
        } else {
            return null
        }
    }

}