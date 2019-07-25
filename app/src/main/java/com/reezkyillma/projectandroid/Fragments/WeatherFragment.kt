@file:Suppress("UNREACHABLE_CODE")

package com.reezkyillma.projectandroid.Fragments

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.hp.weatherapplication.WeatherData.ApixuWeatherApiService
import com.example.hp.weathermodule.Model.ModelMakanan
import com.github.pwittchen.swipe.library.rx2.SwipeListener
import com.reezkyillma.projectandroid.Adapter.RecHomeAdapter
import com.reezkyillma.projectandroid.PreferenceData.PrefData

import com.reezkyillma.projectandroid.R
import com.reezkyillma.projectandroid.WeatherDetailActivity
import kotlinx.android.synthetic.main.fragment_weather.*
import kotlinx.android.synthetic.main.rec_view_makanan.*
import kotlinx.android.synthetic.main.rec_view_minuman.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class WeatherFragment : Fragment() {

    val weather_code = arrayOf("a", "b", "c", "d")
    //    DOKUMENTASI KODE CUACA http://www.apixu.com/doc/Apixu_weather_conditions.json
    val cuaca_dingin = arrayOf("1000", "1003", "1006", "1006", "1030")
    val cuaca_panas = arrayOf("1009", "1063", "1066", "1069", "1072", "1087",
            "1114", "1117", "1135", "1147", "1150", "1153", "1168", "1171", "1180", "1183",
            "1186", "1189", "1192", "1195", "1198", "1201", "1204", "1207", "1210", "1213",
            "1216", "1219", "1222", "1225", "1237", "1240", "1243", "1246", "1249", "1252",
            "1255", "1258", "1261", "1264", "1273", "1276", "1279", "1282")



    fun checkConnection(): Boolean {
        //CHECK THE CONNECTION ON DEVICE
        val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        val isConnectivityManager: Boolean = activeNetwork?.isConnected == true
        return isConnectivityManager
    }

    fun showDialogInternet() {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Network Error")
                .setMessage("Please check your internet connection and try again")
                .setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
                .setCancelable(true)
        builder.show()
    }

    override fun onResume() {
        super.onResume()
        val isConnected = checkConnection()
        if (isConnected) {
            initialize()
        } else {
            showDialogInternet()
        }
    }

    fun initialize() {
        val pd = ProgressDialog(context)
        pd.setMessage("Loading")
        pd.show()


        swipeRefresh.setOnRefreshListener {
            val isConnected = checkConnection()
            if (isConnected){
                println("IS REFRESHING")
                initialize()
                swipeRefresh.isRefreshing = false
            }else{
                swipeRefresh.isRefreshing = false
                showDialogInternet()
            }
        }

        val city = context?.let { PrefData(it) }
        var cityName = city?.getCityPref().toString()
        println("CITY NAME = " + cityName)

        val listMakananDingin: List<ModelMakanan> = listOf(
//                SUMBER MAKANAN https://www.idntimes.com/food/dining-guide/ayu-anggraeni/10-makanan-cocok-dinikmati-saat-hujan-1/full
                ModelMakanan("Sop Ayam", resources.getString(R.string.desc_sop_ayam), "V8YCccbFa0M", resources.getString(R.string.url_sop_ayam)),
                ModelMakanan("Tongseng", resources.getString(R.string.desc_tongseng), "ISPIX8T2LeA", resources.getString(R.string.url_tongseng)),
                ModelMakanan("Capcay", resources.getString(R.string.desc_capcay), "TvzNF_hBqGs", resources.getString(R.string.url_capcay)),
                ModelMakanan("Gulai", resources.getString(R.string.desc_gulai), "DpERGt7qYQU", resources.getString(R.string.url_gulai)),
                ModelMakanan("Kwetiau", resources.getString(R.string.desc_kwetiau), "96XHRvm9iTc", resources.getString(R.string.url_kwetiau)),
                ModelMakanan("Bakmie Godog", resources.getString(R.string.desc_bakmie_godog), "pnPVDZKuyUY", resources.getString(R.string.url_bakmie_godog)),
                ModelMakanan("Ramen", resources.getString(R.string.desc_ramen), "D5kw2BRZGP0", resources.getString(R.string.url_ramen)),
                ModelMakanan("Tom Yum", resources.getString(R.string.desc_tomyum), "9EEzxJApMrQ", resources.getString(R.string.url_tomyum)),
                ModelMakanan("Laksa", resources.getString(R.string.desc_laksa), "bc7IyaujRP8", resources.getString(R.string.url_laksa)),
                ModelMakanan("Soto Ayam", resources.getString(R.string.desc_soto_ayam), "uL6RK5xPksU", resources.getString(R.string.url_soto_ayam)),
                ModelMakanan("Sop Buntut", resources.getString(R.string.desc_sop_buntut), "I9vKLDalg0c", resources.getString(R.string.url_sop_buntut)),
                ModelMakanan("Ceker Ayam", resources.getString(R.string.desc_ceker_ayam), "mzg5MRQLG1U", resources.getString(R.string.url_ceker_ayam)),
                ModelMakanan("Bakso", resources.getString(R.string.desc_bakso), "BJpGEtyVCII", resources.getString(R.string.url_bakso)),
                ModelMakanan("Sayur Asem", resources.getString(R.string.desc_sayur_asem), "6kPBKKHp0ro", resources.getString(R.string.url_sayur_asem)),
                ModelMakanan("Empal Gentong", resources.getString(R.string.desc_empal_gentong), "W1YOJCFKMko", resources.getString(R.string.url_empal_gentong)),
                ModelMakanan("Sayur Lodeh", resources.getString(R.string.desc_sayur_lodeh), "J74sGLQ_doQ", resources.getString(R.string.url_sayur_lodeh)),
                ModelMakanan("Opor Ayam", resources.getString(R.string.desc_opor_ayam), "MedXmWLWN5U", resources.getString(R.string.url_opor_ayam)),
                ModelMakanan("Sop Iga", resources.getString(R.string.desc_sop_iga), "Fe24LwPtVcA", resources.getString(R.string.url_sop_iga)),
                ModelMakanan("Sate Padang", resources.getString(R.string.desc_sate_padang), "Z5v5jKuRCCA", resources.getString(R.string.url_sate_padang)),
                ModelMakanan("Lontong Sayur", resources.getString(R.string.desc_lontong_sayur), "8CsfNTn2xdI", resources.getString(R.string.url_lontong_sayur))
        )
        val listMakananHangat: List<ModelMakanan> = listOf(
                ModelMakanan("Ayam Kecap", resources.getString(R.string.desc_ayam_kecap), "aaicnQuFJxA", resources.getString(R.string.url_ayam_kecap)),
                ModelMakanan("Nasi Campur", resources.getString(R.string.desc_nasi_campur), "xyN5TAOU0EM", resources.getString(R.string.url_nasi_campur)),
                ModelMakanan("Nasi Rendang", resources.getString(R.string.desc_nasi_rendang), "oOQa8B1adO8", resources.getString(R.string.url_nasi_rendang)),
                ModelMakanan("Nasi Uduk", resources.getString(R.string.desc_nasi_uduk), "kJogSW-xJgE", resources.getString(R.string.url_nasi_uduk)),
                ModelMakanan("Nasi Bebek", resources.getString(R.string.desc_nasi_bebek), "3SXZ3yyo44U", resources.getString(R.string.url_nasi_bebek)),
                ModelMakanan("Nasi Goreng", resources.getString(R.string.desc_nasi_goreng), "M91lC34EHWM", resources.getString(R.string.url_nasi_goreng)),
                ModelMakanan("Ayam Goreng", resources.getString(R.string.desc_ayam_goreng), "mPcbxcP-yP4", resources.getString(R.string.url_ayam_goreng)),
                ModelMakanan("Ayam Gepuk", resources.getString(R.string.desc_ayam_gepuk), "mYswK_8thf4", resources.getString(R.string.url_ayam_gepuk)),
                ModelMakanan("Ayam Geprek", resources.getString(R.string.desc_ayam_geprek), "mYswK_8thf4", resources.getString(R.string.url_ayam_geprek)),
                ModelMakanan("Ayam Bakar", resources.getString(R.string.desc_ayam_bakar), "NTW2nkEyDUY", resources.getString(R.string.url_ayam_bakar)),
                ModelMakanan("Pecel Lele", resources.getString(R.string.desc_pecel_lele), "InZVeiz2z7I", resources.getString(R.string.url_pecel_lele)),
                ModelMakanan("Nasi Kuning", resources.getString(R.string.desc_nasi_kuning), "WIRvYIIRMUs", resources.getString(R.string.url_nasi_kuning))
        )

        val listMinuman: List<ModelMakanan> = listOf(
                ModelMakanan("Minuman 1", "Deskripsi Minuman , minuman ini adalah sdfdsfs", "urlVideo", "https://cdn.moneysmart.id/wp-content/uploads/2018/12/08181221/Makanan-ini-paling-pas-disantap-saat-musim-hujan-turun-700x497.jpg"),
                ModelMakanan("Minuman 2", "Deskripsi Minuman , minuman ini adalah sdfdsfs", "urlVideo", "https://cdn.moneysmart.id/wp-content/uploads/2018/12/08181221/Makanan-ini-paling-pas-disantap-saat-musim-hujan-turun-700x497.jpg"),
                ModelMakanan("Minuman 3", "Deskripsi Minuman , minuman ini adalah sdfdsfs", "urlVideo", "https://cdn.moneysmart.id/wp-content/uploads/2018/12/08181221/Makanan-ini-paling-pas-disantap-saat-musim-hujan-turun-700x497.jpg")
        )
        val listMinumanDingin: List<ModelMakanan> = listOf(
//                SUMBER MAKANAN https://www.idntimes.com/food/dining-guide/ayu-anggraeni/10-makanan-cocok-dinikmati-saat-hujan-1/full
                ModelMakanan("Thai Tea", resources.getString(R.string.desc_thai_tea), "vWn-qoHTgss", resources.getString(R.string.url_thai_tea)),
                ModelMakanan("Bandrek", resources.getString(R.string.desc_bandrek), "iXdUMqL4-lA", resources.getString(R.string.url_bandrek)),
                ModelMakanan("Wedang Ronde", resources.getString(R.string.desc_wedang_ronde), "6SaJk4otj38", resources.getString(R.string.url_wedang_ronde)),
                ModelMakanan("Wedang Jahe", resources.getString(R.string.desc_wedang_jahe), "h_9rt-MGlTY", resources.getString(R.string.url_wedang_jahe)),
                ModelMakanan("Bir Pletok", resources.getString(R.string.desc_bir_pletok), "5rmZ7fbbnz8", resources.getString(R.string.url_bir_pletok)),
                ModelMakanan("Teh Tarik", resources.getString(R.string.desc_teh_tarik), "kxlYDTt-aZQ", resources.getString(R.string.url_teh_tarik)),
                ModelMakanan("Hot Chocolate", resources.getString(R.string.desc_hot_chocolate), "6JggavEMEeo", resources.getString(R.string.url_hot_chocolate)),
                ModelMakanan("Moccacino coffee ", resources.getString(R.string.desc_moccacino_coffee), "WxyinaLxqJo", resources.getString(R.string.url_moccacino_coffee)),
                ModelMakanan("Susu Jahe", resources.getString(R.string.desc_susu_jahe), "BoyoqOCg5Fs", resources.getString(R.string.url_susu_jahe)),
                ModelMakanan("Bajigur", resources.getString(R.string.desc_bajigur), "6Ld-UXuKKDY", resources.getString(R.string.url_bajigur)),
                ModelMakanan("Hot Milo", resources.getString(R.string.desc_hot_milo), "cTkby0BRE3Y", resources.getString(R.string.url_hot_milo)),
                ModelMakanan("Teh Talua ", resources.getString(R.string.desc_teh_talua), "tmVLeEePPew", resources.getString(R.string.url_teh_talua)),
                ModelMakanan("Kembang Tahu", resources.getString(R.string.desc_kembang_tahu), "DmGc7HOPkmU", resources.getString(R.string.url_kembang_tahu)),
                ModelMakanan("Sekoteng", resources.getString(R.string.desc_sekoteng), "1IGqMeTeVGs", resources.getString(R.string.url_sekoteng)),
                ModelMakanan("Sarabba", resources.getString(R.string.desc_sarabba), "IRlI223rtPo", resources.getString(R.string.url_sarabba)),
                ModelMakanan("Jamu", resources.getString(R.string.desc_jamu), "uyeNx5xYQk", resources.getString(R.string.url_jamu))
        )
        val listMinumanHangat: List<ModelMakanan> = listOf(
                ModelMakanan("Strawberry Milkshake", resources.getString(R.string.desc_strawberry_milkshake), "G46-I9Ri1sQ", resources.getString(R.string.url_strawberry_milkshake)),
                ModelMakanan("Es Cendol", resources.getString(R.string.desc_es_cendol), "t9jhhaF5CLc", resources.getString(R.string.url_es_cendol)),
                ModelMakanan("Es Campur", resources.getString(R.string.desc_es_campur), "KcjHgefz-Vc", resources.getString(R.string.url_es_campur)),
                ModelMakanan("Es Kepal Milo", resources.getString(R.string.desc_es_kepal_milo), "9S3xhVnWWDc", resources.getString(R.string.url_es_kepal_milo)),
                ModelMakanan("Es Cincau", resources.getString(R.string.desc_es_cincau), "GuX1d-w07v8", resources.getString(R.string.url_es_cincau)),
                ModelMakanan("Es Podeng", resources.getString(R.string.desc_es_podeng), "W8jzl3ivm_w", resources.getString(R.string.url_es_podeng)),
                ModelMakanan("Es Selendang Mayang", resources.getString(R.string.desc_selendang_mayang), "UcQlBa3og4U", resources.getString(R.string.url_selendang_mayang)),
                ModelMakanan("Es Kelapa Muda ", resources.getString(R.string.desc_kelapa_muda), "HxvhjPCCOac", resources.getString(R.string.url_kelapa_muda)),
                ModelMakanan("Es Jeruk", resources.getString(R.string.desc_es_jeruk), "mvYo9bo44p8", resources.getString(R.string.url_es_jeruk)),
                ModelMakanan("Es Serut", resources.getString(R.string.desc_es_serut), "5egPQUu_GnA", resources.getString(R.string.url_es_serut)),
                ModelMakanan("Es Kopyor", resources.getString(R.string.desc_es_kopyor), "NNOkJVBtY-4", resources.getString(R.string.url_es_kopyor)),
                ModelMakanan("Lemon Tea", resources.getString(R.string.desc_lemon_tea), "KVFB2SrwM3Q", resources.getString(R.string.url_lemon_tea)),
                ModelMakanan("Frappucino", resources.getString(R.string.desc_frappucino), "ozxq3ji42bA", resources.getString(R.string.url_frappuccino)),
                ModelMakanan("Espresso Chill", resources.getString(R.string.desc_espresso_chill), "KeFUzPXOJhY", resources.getString(R.string.url_espresso_chill)),
                ModelMakanan("Cold Brew", resources.getString(R.string.desc_cold_brew), "U0D3CtaJMMI", resources.getString(R.string.url_cold_brew)),
                ModelMakanan("Iced Taro Latte", resources.getString(R.string.desc_ice_taro_latte), "WQ4KWh6yQGs", resources.getString(R.string.url_ice_taro_latte)),
                ModelMakanan("Es Doger", resources.getString(R.string.desc_es_doger), "TO71hZJ95xs", resources.getString(R.string.url_es_doger)),
                ModelMakanan("Es Nona", resources.getString(R.string.desc_es_nona), "NGU7fJEH-_M", resources.getString(R.string.url_es_nona)),
                ModelMakanan("Es Tambring", resources.getString(R.string.desc_es_tambring), "OLk5Ta7RAgQ", resources.getString(R.string.url_es_tambring)),
                ModelMakanan("Es Laksamana Mengamuk", resources.getString(R.string.desc_es_laksamana_mengamuk), "lkyZ_kRfZ9c", resources.getString(R.string.url_es_laksamana_mengamuk)),
                ModelMakanan("Es Serbat Kweni", resources.getString(R.string.desc_es_serbat_kweni), "KSZFxrUS-a4", resources.getString(R.string.url_es_serbat_kweni)),
                ModelMakanan("Es Kacang Merah", resources.getString(R.string.desc_es_kacang_merah), "2HUc2nmK_mM", resources.getString(R.string.url_es_kacang_merah)),
                ModelMakanan("Es Gempol Pleret", resources.getString(R.string.desc_es_gempol_pleret), "1dBTlR5x1mU", resources.getString(R.string.url_es_gempol_pleret)),
                ModelMakanan("Es Oyen ", resources.getString(R.string.desc_es_oyen), "Tdyt98lAoQM", resources.getString(R.string.url_es_oyen)),
                ModelMakanan("Es Lontrong Slawi", resources.getString(R.string.desc_es_lontrong_slawi), "EYJ8X4Zy888", resources.getString(R.string.url_es_lontrong_slawi)),
                ModelMakanan("Es Pisang Ijo ", resources.getString(R.string.desc_es_pisang_ijo), "9D9B0TFGqzs", resources.getString(R.string.url_es_pisang_ijo)),
                ModelMakanan("Es Teler", resources.getString(R.string.desc_es_teler), "eYfDwAuTY_E", resources.getString(R.string.url_es_teler))


                )

        val apiService = ApixuWeatherApiService()


        val launch = GlobalScope.launch(Dispatchers.Main) {
            val currentWeatherResponse = apiService.getCurrentWeather(cityName)
                    .await()


            Condition.text = (currentWeatherResponse.currentWeatherEntry.condition.text.toString())

            if (currentWeatherResponse.location.name != null){
                lokasi.text = (currentWeatherResponse.location.name)
            }else{
                lokasi.text = (cityName)
            }

            temperatur.text = ((currentWeatherResponse.currentWeatherEntry.tempC.toString()).substring(0, 2))
            println("CONDITION = " + currentWeatherResponse.currentWeatherEntry.condition.toString())
            println("LOCATION  = " + currentWeatherResponse.location.name)
            println("TEMPERATURE = " + (currentWeatherResponse.currentWeatherEntry.tempC.toString()).substring(0, 2))


            var kodeCuaca = currentWeatherResponse.currentWeatherEntry.condition.code.toString()

            food_rec_view.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            minuman_rec_view.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

            if (cuaca_panas.contains(kodeCuaca)) {
                food_rec_view.adapter = RecHomeAdapter(listMakananDingin)
                minuman_rec_view.adapter = RecHomeAdapter(listMinumanDingin)
                header_bg.setImageResource(R.drawable.bg_hujan)
            } else {
                food_rec_view.adapter = RecHomeAdapter(listMakananHangat)
                minuman_rec_view.adapter = RecHomeAdapter(listMinumanHangat)
                header_bg.setImageResource(R.drawable.bg_cerah)
                Condition.setTextColor(resources.getColor(R.color.colorWhite))
                lokasi.setTextColor(resources.getColor(R.color.colorWhite))
                temperatur.setTextColor(resources.getColor(R.color.colorWhite))

            }
        }


        temperatur.setOnClickListener(View.OnClickListener {
            val intentToWeather = Intent(context, WeatherDetailActivity::class.java)
            intentToWeather.putExtra("CITYNAME", cityName)
            startActivity(intentToWeather)
        })

        pd.dismiss()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_weather, container, false)
        val isConnected = checkConnection()
        if (isConnected) {
            initialize()
        } else {
            showDialogInternet()
        }


    }


}
