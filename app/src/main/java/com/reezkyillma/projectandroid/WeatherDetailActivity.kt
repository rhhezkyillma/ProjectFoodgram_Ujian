package com.reezkyillma.projectandroid

import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.reezkyillma.projectandroid.PreferenceData.PrefData
import com.reezkyillma.projectandroid.databinding.ActivityWeatherDetailBinding

class WeatherDetailActivity : AppCompatActivity() {

    private lateinit var databinding: ActivityWeatherDetailBinding

    companion object {
        var globalVar = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather_detail)
        val bundleData =intent.extras
        val kota = bundleData?.getString("CITYNAME", "Semarang")!!
        globalVar = kota
        databinding = DataBindingUtil.setContentView(this, R.layout.activity_weather_detail)
        databinding.vm = WeatherDetailViewModel()
    }

    fun getCityName(): String {
        val bundleData =this.intent.extras
        val Str = bundleData?.getString("CITYNAME", "Semarang")

        return Str.toString()
    }
}