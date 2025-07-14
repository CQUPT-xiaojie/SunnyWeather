package com.sunnyweather.andriod.ui.place

import android.location.Address
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.sunnyweather.andriod.logic.Repository
import com.sunnyweather.andriod.logic.model.Place

class PlaceViewModel: ViewModel() {
    private val searchLiveData = MutableLiveData<String>()

    val placeList = ArrayList<Place>()

    val placeLiveData = searchLiveData.switchMap { query -> Repository.searchPlaces(query) }

    fun searchPlaces(query: String) {
        searchLiveData.value = query
    }

    private val _frequentPlaceListLiveData = MutableLiveData<ArrayList<Place>>()

    val frequentPlaceListLiveData: LiveData<ArrayList<Place>>
        get() = _frequentPlaceListLiveData

    fun savePlace(placeAddress: String, place: Place) = Repository.savePlace(placeAddress, place)

    fun removePlace(placeAddress: String) = Repository.removePlace(placeAddress)

    fun clearPlace() = Repository.clearPlace()

    fun setHome(place: Place) = Repository.setHome(place)

    fun getSavedPlace(placeAddress: String) = Repository.getSavedPlace(placeAddress)

    fun isPlaceSaved(placeAddress: String) = Repository.isPlaceSaved(placeAddress)

    fun loadAllPlace() = Repository.loadAllPlace()

    fun refreshList() {
        _frequentPlaceListLiveData.value = ArrayList(loadAllPlace())
    }
}