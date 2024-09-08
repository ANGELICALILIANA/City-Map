package com.example.citymap.service

import com.example.citymap.dto.LocationDTO
import com.example.citymap.utils.Constants
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