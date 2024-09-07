package com.example.citymap

import com.example.citymap.DTO.CoordinateDTO
import com.example.citymap.DTO.WeatherDTO
import retrofit2.http.GET

interface IApiClient {

    @GET
    suspend fun getData(
        coordinate: CoordinateDTO,
        apiKey: String = Constants.API_KEY,
    ): retrofit2.Response<WeatherDTO>

}