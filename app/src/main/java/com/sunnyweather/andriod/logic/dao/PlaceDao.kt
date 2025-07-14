package com.sunnyweather.andriod.logic.dao

import android.content.Context
import androidx.core.content.edit
import com.google.gson.Gson
import com.sunnyweather.andriod.SunnyWeatherApplication
import com.sunnyweather.andriod.logic.model.Place

object PlaceDao {
    fun savePlace(placeAddress: String, place: Place) {
        sharedPreferences().edit {
            putString(placeAddress, Gson().toJson(place))
        }
    }

    fun removePlace(placeAddress: String) {
        sharedPreferences().edit() {
            remove(placeAddress)
        }
    }

    fun setHome(placeAddress: String) {
        sharedPreferences().edit() {
            putString("home", placeAddress)
        }
    }

    fun getHome(): String?  {
        val address = sharedPreferences().getString("home", "")
        return address
    }

    fun getSavedPlace(placeAddress: String): Place {
        val placeJson = sharedPreferences().getString(placeAddress, "")
        return Gson().fromJson(placeJson, Place::class.java)
    }

    fun clearPlace() {
        val allEntries= sharedPreferences().all
        if (allEntries.size == 2) {
            sharedPreferences().edit() {
                clear()
            }
        }
    }

    fun isPlaceSaved(placeAddress: String) = sharedPreferences().contains(placeAddress)

    private fun sharedPreferences() = SunnyWeatherApplication
        .context.getSharedPreferences("sunny_weather_place", Context.MODE_PRIVATE)

    fun loadAllPlace(): List<Place> = sharedPreferences().all
        .filterValues { it is String }
        .filter { (key, _) -> key != "home" }
        .map { (_, jsonString) ->
            Gson().fromJson(jsonString as String, Place::class.java) }
}