package com.example.citymap.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.citymap.VO.LocationVO
import com.example.citymap.IWeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherRepository: IWeatherRepository
): ViewModel() {

    private val weather = MutableLiveData<LocationVO>()
    fun getLocation(): LiveData<LocationVO> = weather

    private suspend fun sendCoordinates(latitude: Float = 4F, longitude: Float = 12F) {
        weatherRepository.sendDataWeather(latitude, longitude)
    }

    private fun getWeather() {

    }

}