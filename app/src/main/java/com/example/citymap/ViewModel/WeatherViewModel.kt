package com.example.citymap.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.citymap.VO.LocationVO
import com.example.citymap.IWeatherRepository
import com.example.citymap.VO.LocationDatabaseVO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherRepository: IWeatherRepository
): ViewModel() {

    private val weather = MutableLiveData<LocationVO>()
    fun getLocation(): LiveData<LocationVO> = weather

    private val _locationDatabaseVO = MutableLiveData<LocationDatabaseVO>()

    val locationDatabaseVO: LiveData<LocationDatabaseVO>
        get() = _locationDatabaseVO

    suspend fun sendCoordinates(latitude: Double, longitude: Double) {
        weatherRepository.sendDataWeather(latitude, longitude)
    }

    private fun getWeather() {

    }


    fun insertLocation(locationEntity: LocationDatabaseVO) {
        viewModelScope.launch {
            weatherRepository.insertLocation(locationEntity)
        }
    }

    fun searchLocationByName(query: String) {
        viewModelScope.launch {
            _locationDatabaseVO.postValue(weatherRepository.searchLocationByName(query))
        }
    }

}