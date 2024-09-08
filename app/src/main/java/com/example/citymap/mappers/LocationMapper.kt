package com.example.citymap.mappers

import com.example.citymap.dto.LocationDTO
import com.example.citymap.vo.CurrentVO
import com.example.citymap.vo.LocationVO
import com.example.citymap.vo.WeatherVO
import com.example.citymap.database.entity.LocationEntity

object LocationMapper {

    fun locationVoToDto(locationDTO: LocationDTO): LocationVO {
        return locationDTO.let {
            LocationVO(
                latitude = it.latitude,
                longitude = it.longitude,
                timezone = it.timezone,
                timezoneOffset = it.timezoneOffset,
                current = it.current.let {
                    CurrentVO(
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
                            WeatherVO(
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

    fun locationVoToEntity(locationVO: LocationVO, name: String): LocationEntity {
        return locationVO.let {
            LocationEntity(
                latitude = it.latitude,
                longitude = it.longitude,
                timezone = it.timezone,
                timezoneOffset = it.timezoneOffset,
                currentTime = it.current?.currentTime ?: 0,
                sunrise = it.current?.sunrise ?: 0,
                sunset = it.current?.sunset ?: 0,
                temperature = it.current?.temperature ?: 0F,
                feelsLike = it.current?.feelsLike ?: 0F,
                pressure = it.current?.pressure ?: 0,
                humidity = it.current?.humidity ?: 0,
                dewPoint = it.current?.dewPoint ?: 0F,
                uv = it.current?.uv ?: 0F,
                clouds = it.current?.clouds ?: 0,
                visibility = it.current?.visibility ?: 0,
                windSpeed = it.current?.windSpeed ?: 0F,
                windDirection = it.current?.windDirection ?: 0,
                windGust = it.current?.windGust ?: 0F,
                idWeather = it.current?.weather?.first()?.idWeather ?: 0,
                mainWeather = it.current?.weather?.first()?.mainWeather ?: "",
                descriptionWeather = it.current?.weather?.first()?.descriptionWeather ?: "",
                iconWeather = it.current?.weather?.first()?.iconWeather ?: "",
                name = name
            )
        }
    }

    fun locationEntityToVO(locationEntity: LocationEntity): LocationVO {
        return locationEntity.let {
            LocationVO(
                latitude = it.latitude,
                longitude = it.longitude,
                timezone = it.timezone,
                timezoneOffset = it.timezoneOffset,
                name = it.name,
                current = CurrentVO(
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
                    weather = listOf(
                        WeatherVO(
                            idWeather = it.idWeather,
                            mainWeather = it.mainWeather,
                            descriptionWeather = it.descriptionWeather,
                            iconWeather = it.iconWeather
                        )
                    )
                )
            )
        }
    }
}