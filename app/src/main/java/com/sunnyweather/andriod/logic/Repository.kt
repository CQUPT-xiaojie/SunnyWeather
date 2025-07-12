package com.sunnyweather.andriod.logic

import androidx.lifecycle.liveData
import com.sunnyweather.andriod.SunnyWeatherApplication
import com.sunnyweather.andriod.logic.model.Place
import com.sunnyweather.andriod.logic.network.SunnyWeatherNetwork
import kotlinx.coroutines.Dispatchers
import java.lang.Exception


object Repository {
    // 这里的liveData提供了协程作用域，并利用Dispaters.IO起了一个线程来执行
    fun searchPlaces(query: String) = liveData(Dispatchers.IO) {
        val result = try {
            val placeResponse = SunnyWeatherNetwork.searchPlaces(query)
            if (placeResponse.status == "ok") {
                val places = placeResponse.places
                Result.success(places)
            } else {
                Result.failure(RuntimeException("response status is ${placeResponse.status}"))
            }
        } catch (e: Exception) {
            Result.failure<List<Place>>(e)
        }
        emit(result)
    }
}