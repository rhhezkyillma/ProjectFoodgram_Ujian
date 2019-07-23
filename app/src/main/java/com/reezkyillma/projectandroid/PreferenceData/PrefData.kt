package com.reezkyillma.projectandroid.PreferenceData

import android.content.Context

class PrefData(context: Context) {

    val CITY_PREFERENCE = "CityPreference"
    val cityPreference = context.getSharedPreferences(CITY_PREFERENCE, Context.MODE_PRIVATE)

    fun getCityPref() : String{
        return cityPreference.getString(CITY_PREFERENCE, "Jakarta")
    }

    fun setCityPref(city : String){
        val editor = cityPreference.edit()
        editor.putString(CITY_PREFERENCE, city)
        editor.apply()
    }
}