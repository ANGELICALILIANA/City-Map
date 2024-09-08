package com.example.citymap.Mappers

import com.example.citymap.DTO.CurrentDTO
import com.example.citymap.DTO.LocationDTO
import com.example.citymap.DTO.WeatherDTO
import com.example.citymap.VO.LocationDatabaseVO
import com.example.citymap.VO.LocationVO
import com.example.citymap.database.entity.LocationEntity

object LocationMapper {

    // De LocationEntity a LocationDatabaseVO
    fun entityToVO(entity: LocationEntity): LocationDatabaseVO {
        return LocationDatabaseVO(
            latitude = entity.latitude,
            longitude = entity.longitude,
            name = entity.name
        )
    }

    // De LocationDatabaseVO a LocationEntity
    fun voToEntity(vo: LocationDatabaseVO): LocationEntity {
        return LocationEntity(
            name = vo.name,
            latitude = vo.latitude,
            longitude = vo.longitude
        )
    }

    fun locationVoToDto(locationDTO: LocationDTO): LocationVO {
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