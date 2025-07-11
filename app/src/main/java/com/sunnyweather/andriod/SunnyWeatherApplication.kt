package com.sunnyweather.andriod

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

class SunnyWeatherApplication: Application() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
        const val TOKEN = "joF7Vc24k1huaSUE"
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}