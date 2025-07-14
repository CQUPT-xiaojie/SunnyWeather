package com.sunnyweather.andriod.ui.weather

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.sunnyweather.andriod.logic.Repository
import com.sunnyweather.andriod.logic.model.Location
import com.sunnyweather.andriod.logic.model.Weather

class WeatherViewModel: ViewModel() {
    private val locationLiveData = MutableLiveData<Location>()

    var locationLng = ""
    var locationLat = ""
    var placeName = ""

    val weatherLiveData = locationLiveData.switchMap { location ->
        Repository.refreshWeather(location.lng, location.lat)
    }

    fun refreshWeather(lng: String, lat: String) {
        locationLiveData.value = Location(lng, lat)
    }

    fun saveWeather(placeName: String, weather: Weather) =
        Repository.saveWeather(placeName, weather)

    fun getSavedWeather(placeName: String) =
        Repository.getSavedWeather(placeName)
}