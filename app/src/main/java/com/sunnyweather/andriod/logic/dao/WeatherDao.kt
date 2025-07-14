package com.sunnyweather.andriod.logic.dao

import android.content.Context
import androidx.core.content.edit
import com.google.gson.Gson
import com.sunnyweather.andriod.SunnyWeatherApplication
import com.sunnyweather.andriod.logic.model.Place
import com.sunnyweather.andriod.logic.model.Weather

object WeatherDao {
    fun saveWeather(placeName: String, weather: Weather) {
        sharedPreferences().edit {
            putString(placeName, Gson().toJson(weather))
        }
    }

    fun getSavedWeather(placeName: String): Weather {
        val weatherJson = sharedPreferences().getString(placeName, "")
        return Gson().fromJson(weatherJson, Weather::class.java)
    }

    private fun sharedPreferences() = SunnyWeatherApplication
        .context.getSharedPreferences("sunny_weather_weather", Context.MODE_PRIVATE)
}