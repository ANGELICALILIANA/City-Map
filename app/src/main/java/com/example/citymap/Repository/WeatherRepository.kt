package com.example.citymap.Repository

import com.example.citymap.Mapper
import com.example.citymap.VO.LocationVO
import com.example.citymap.VO.WeatherVO
import com.example.citymap.database.AppDatabase
import com.example.citymap.IApiClient
import com.example.citymap.IWeatherRepository
import com.example.citymap.Mappers.LocationMapper.entityToVO
import com.example.citymap.Mappers.LocationMapper.voToEntity
import com.example.citymap.VO.LocationDatabaseVO
import com.example.citymap.database.entity.LocationEntity
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    //private val apiClient : IApiClient,
    private val dataBase : AppDatabase,
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

    override suspend fun insertLocation(locationVO: LocationDatabaseVO) {
        val locationEntity = voToEntity(locationVO)
        dataBase.locationDao().insert(locationEntity)
    }

    override suspend fun searchLocationByName(query: String): LocationDatabaseVO? {
        val locationEntity = dataBase.locationDao().searchByName(query)
        return locationEntity?.let { entityToVO(it) }
    }

}