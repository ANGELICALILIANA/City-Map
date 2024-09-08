package com.example.citymap

import com.example.citymap.DTO.CoordinateDTO
import com.example.citymap.DTO.LocationDTO
import com.example.citymap.DTO.WeatherDTO
import com.example.citymap.VO.LocationVO
import retrofit2.http.GET
import retrofit2.http.Query

interface IApiClient {

    @GET("onecall")
    suspend fun getData(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String = Constants.API_KEY
    ): retrofit2.Response<LocationDTO>

}