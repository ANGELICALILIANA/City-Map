package com.example.citymap

import com.example.citymap.DTO.CurrentDTO
import com.example.citymap.DTO.LocationDTO
import com.example.citymap.DTO.WeatherDTO
import com.example.citymap.VO.LocationVO
import com.example.citymap.VO.WeatherVO

class Mapper {

    fun locationVO(locationDTO: LocationDTO): LocationVO {
        return locationDTO.let {
            LocationVO(
                latitude = it.latitude,
                longitude = it.longitude,
                timezone = it.timezone,
                timezoneOffset = it.timezoneOffset,
                current = it.current.let {
                    CurrentDTO(
                        currentTime = it.currentTime,
                        sunrise = it.sunrise,
                        sunset = it.sunset,
                        temperature = it.temperature,
                        feelsLike = it.feelsLike,
                        pressure = it.pressure,
                        humidity = it.humidity,
                        dewPoint = it.dewPoint,
                        uv = it.uv,
                        clouds = it.clouds,
                        visibility = it.visibility,
                        windSpeed = it.windSpeed,
                        windDirection = it.windDirection,
                        windGust = it.windGust,
                        weather = it.weather.map {
                            WeatherDTO(
                                idWeather = it.idWeather,
                                mainWeather = it.mainWeather,
                                descriptionWeather = it.descriptionWeather,
                                iconWeather = it.iconWeather
                            )
                        }
                    )
                }
            )
        }
    }

}