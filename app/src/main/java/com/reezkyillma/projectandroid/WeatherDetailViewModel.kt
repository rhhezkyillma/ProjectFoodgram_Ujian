package com.reezkyillma.projectandroid

import android.app.Application
import android.arch.lifecycle.ViewModel
import android.content.Context
import android.content.Intent
import android.databinding.ObservableField
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.hp.weatherapplication.WeatherData.ApixuWeatherApiService
import com.reezkyillma.projectandroid.Fragments.WeatherFragment
import com.reezkyillma.projectandroid.PreferenceData.PrefData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import com.reezkyillma.projectandroid.WeatherDetailActivity.Companion.globalVar
import kotlin.coroutines.coroutineContext

class WeatherDetailViewModel : ViewModel() {
    val temperatureC = ObservableField<String>("0")
    val temperatureF = ObservableField<String>("0")
    val tekananUdara = ObservableField<String>("0")
    val kelembaban = ObservableField<String>("0")
    val kondisi = ObservableField<String>("0")
    val lastUpdate = ObservableField<String>("0")
    val uv = ObservableField<String>("0")
    val kecepatanAngin = ObservableField<String>("0")
    val kota = ObservableField<String>("0")
    val provinsi = ObservableField<String>("0")
    val negara = ObservableField<String>("0")

    //    nanti tanyain gimana caranyangambil context buat kodingan dibawah
    //    val cityName = PrefData(context).getCityPref()

    init {
        val apiService = ApixuWeatherApiService()
        println("CITY NAME ON VIEW MODEL ====> " + globalVar)
        val launch = GlobalScope.launch(Dispatchers.Main) {
            val currentWeatherResponse = apiService.getCurrentWeather(globalVar).await()
            temperatureC.set(currentWeatherResponse.currentWeatherEntry.tempC.toString()) //done
            temperatureF.set(currentWeatherResponse.currentWeatherEntry.tempF.toString())
            tekananUdara.set(currentWeatherResponse.currentWeatherEntry.pressureIn.toString())
            kelembaban.set("Humidity : " + currentWeatherResponse.currentWeatherEntry.humidity.toString())
            kondisi.set(currentWeatherResponse.currentWeatherEntry.condition.text)
            lastUpdate.set(currentWeatherResponse.currentWeatherEntry.lastUpdated)
            uv.set(currentWeatherResponse.currentWeatherEntry.uv.toString())
             kecepatanAngin.set("Wind : " + currentWeatherResponse.currentWeatherEntry.gustKph.toString() + " km/h")
            kota.set(currentWeatherResponse.location.name)
            provinsi.set(currentWeatherResponse.location.region)
            negara.set(", " + currentWeatherResponse.location.country)
        }
    }


}