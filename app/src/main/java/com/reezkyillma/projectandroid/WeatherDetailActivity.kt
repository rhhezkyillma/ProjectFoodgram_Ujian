package com.reezkyillma.projectandroid

import android.app.ProgressDialog
import android.content.Intent
import android.databinding.DataBindingUtil
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.reezkyillma.projectandroid.PreferenceData.PrefData
import com.reezkyillma.projectandroid.databinding.ActivityWeatherDetailBinding
import kotlinx.android.synthetic.main.activity_weather_detail.*

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

        val pd = ProgressDialog(this)
        pd.setMessage("Loading")

        globalVar = kota
        databinding = DataBindingUtil.setContentView(this, R.layout.activity_weather_detail)
        databinding.vm = WeatherDetailViewModel()
        pd.dismiss()

        arrow_back.setOnClickListener {
            onBackPressed()
        }
        pin_location.setOnClickListener {
            val gmmIntentUri = Uri.parse("geo:0,0?q=$globalVar")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            startActivity(mapIntent)
        }
    }

    fun getCityName(): String {
        val bundleData =this.intent.extras
        val Str = bundleData?.getString("CITYNAME", "Semarang")
        return Str.toString()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}