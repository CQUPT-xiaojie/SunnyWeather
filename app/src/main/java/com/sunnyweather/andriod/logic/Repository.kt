package com.sunnyweather.andriod.logic

import androidx.lifecycle.liveData
import com.sunnyweather.andriod.logic.dao.PlaceDao
import com.sunnyweather.andriod.logic.dao.WeatherDao
import com.sunnyweather.andriod.logic.model.Place
import com.sunnyweather.andriod.logic.model.Weather
import com.sunnyweather.andriod.logic.network.SunnyWeatherNetwork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.lang.Exception
import kotlin.coroutines.CoroutineContext


object Repository {
    // 这里的liveData提供了协程作用域，并利用Dispaters.IO起了一个线程来执行
//    fun searchPlaces(query: String) = liveData(Dispatchers.IO) {
//        val result = try {
//            val placeResponse = SunnyWeatherNetwork.searchPlaces(query)
//            if (placeResponse.status == "ok") {
//                val places = placeResponse.places
//                Result.success(places)
//            } else {
//                Result.failure(RuntimeException("response status is ${placeResponse.status}"))
//            }
//        } catch (e: Exception) {
//            Result.failure<List<Place>>(e)
//        }
//        emit(result)
//    }

    fun searchPlaces(query: String) = fire(Dispatchers.IO) {
        val placeResponse = SunnyWeatherNetwork.searchPlaces(query)
        if (placeResponse.status == "ok") {
            val places = placeResponse.places
            Result.success(places)
        } else {
            Result.failure(RuntimeException("response status is ${placeResponse.status}"))
        }
    }

    fun refreshWeather(lng: String, lat: String) = fire(Dispatchers.IO) {
        coroutineScope {
            val deferredRealtime = async {
                SunnyWeatherNetwork.getRealtimeWeather(lng, lat)
            }
            val deferredDaily = async {
                SunnyWeatherNetwork.getDailyWeather(lng, lat)
            }
            val realtimeResponse = deferredRealtime.await()
            val dailyResponse = deferredDaily.await()
            if (realtimeResponse.status == "ok" && dailyResponse.status == "ok") {
                val weather = Weather(realtimeResponse.result.realtime, dailyResponse.result.daily)
                Result.success(weather)
            } else {
                Result.failure(RuntimeException(
                    "realtime response status is ${realtimeResponse.status}" +
                            "daily response status is ${dailyResponse.status}")
                )
            }
        }
    }

    private fun <T> fire(context: CoroutineContext, block: suspend () -> Result<T>) =
        liveData<Result<T>>(context) {
            val result = try {
                block()
            } catch (e: Exception) {
                Result.failure<T>(e)
            }
            emit(result)
        }

    fun savePlace(placeAddress:String, place: Place) = PlaceDao.savePlace(placeAddress ,place)

    fun removePlace(placeAddress: String) = PlaceDao.removePlace(placeAddress)

    fun clearPlace() = PlaceDao.clearPlace()

    fun setHome(placeAddress: String) = PlaceDao.setHome(placeAddress)

    fun getHome() = PlaceDao.getHome()

    fun getSavedPlace(placeAddress: String) = PlaceDao.getSavedPlace(placeAddress)

    fun isPlaceSaved(placeAddress: String) = PlaceDao.isPlaceSaved(placeAddress)

    fun loadAllPlace() = PlaceDao.loadAllPlace()

    fun saveWeather(placeName: String, weather: Weather) =
        WeatherDao.saveWeather(placeName, weather)

    fun getSavedWeather(placeName: String) =
        WeatherDao.getSavedWeather(placeName)
}