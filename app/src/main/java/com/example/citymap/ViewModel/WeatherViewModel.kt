package com.example.citymap.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.citymap.IWeatherRepository
import com.example.citymap.VO.LocationVO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherRepository: IWeatherRepository
): ViewModel() {

    private val _weather = MutableLiveData<LocationVO>()
    private val _locationDatabaseVO = MutableLiveData<LocationVO>()

    val weather: LiveData<LocationVO>
        get() = _weather
    val locationDatabaseVO: LiveData<LocationVO>
        get() = _locationDatabaseVO

    fun sendCoordinates(latitude: Double, longitude: Double, name: String) {
        viewModelScope.launch {
            _weather.postValue(weatherRepository.sendDataWeather(latitude, longitude, name))
        }
    }

    fun searchLocationByName(query: String) {
        viewModelScope.launch {
            _locationDatabaseVO.postValue(weatherRepository.searchLocationByName(query))
        }
    }

}