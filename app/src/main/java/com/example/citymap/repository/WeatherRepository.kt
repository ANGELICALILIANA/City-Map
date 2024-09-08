package com.example.citymap.repository

import com.example.citymap.service.IApiClient
import com.example.citymap.mappers.LocationMapper
import com.example.citymap.vo.LocationVO
import com.example.citymap.database.AppDatabase
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val apiClient : IApiClient,
    private val dataBase : AppDatabase,
): IWeatherRepository {

    override suspend fun getDataWeather() {
        TODO("Not yet implemented")
    }

    override suspend fun sendDataWeather(latitude: Double, longitude: Double, name: String): LocationVO? {
        val response = apiClient.getData(latitude, longitude)
        if (response.isSuccessful) {
            if (response.body() != null) {
                val locationVO = LocationMapper.locationVoToDto(response.body()!!)
                insertLocation(locationVO, name)
                return locationVO
            } else {
                return LocationVO()
            }
        } else {
            return null
        }
    }

   suspend fun insertLocation(locationVO: LocationVO, name: String) {
       val existingLocation = dataBase.locationDao().getLocationByName(name)
       if (existingLocation == null) {
           val locationEntity = LocationMapper.locationVoToEntity(locationVO, name)
           dataBase.locationDao().insert(locationEntity)
       }

    }

    override suspend fun searchLocationByName(query: String): LocationVO? {
        val locationEntity = dataBase.locationDao().searchByName(query)
        return locationEntity?.let { LocationMapper.locationEntityToVO(it) }
    }

}