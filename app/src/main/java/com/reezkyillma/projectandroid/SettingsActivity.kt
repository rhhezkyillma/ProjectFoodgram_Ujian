package com.reezkyillma.projectandroid

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import com.reezkyillma.projectandroid.PreferenceData.PrefData
import kotlinx.android.synthetic.main.activity_main__message.*
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.activity_settings.back

class SettingsActivity : AppCompatActivity() {

    var languages = arrayOf("Ambon", "Balikpapan", "Bandung", "Banjarmasin","Batam", "Bekasi", "Bengkulu", "Bogor", "Cilegon",
            "Cirebon","Denpasar","Garut", "Gorontalo", "Gresik","Jakarta", "Jambi", "Jayapura", "Jember", "Karawang","Kediri",
            "Kisaran", "Kupang", "Lampung", "Makassar", "Malang", "Manado", "Mataram", "Medan", "Padang", "Palangkaraya", "Palu",
            "Palembang", "Palu", "Pekalongan", "Pekanbaru", "Pontianak","Samarinda", "Semarang", "Sidoarjo", "Singkawang", "Solo",
            "Sumedang","Surabaya", "Tangerang", "Tasikmalaya", "Ternate", "Yogyakarta")

    var spinner: Spinner? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val adapter = ArrayAdapter(
                this, // Context
                android.R.layout.simple_spinner_item, // Layout
                languages // Array
        )

        back.setOnClickListener {
            val intentMessage = Intent(this@SettingsActivity,MainActivity::class.java)
            startActivity(intentMessage)
        }


        // Set the drop down view resource
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)

        // Finally, data bind the spinner object with dapter
        var temp_city = PrefData(this).getCityPref()
        spinnerCity.prompt = temp_city
        spinnerCity.adapter = adapter

        // Set an on item selected listener for spinner object
        spinnerCity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                // Display the selected item text on text view
//                Toast.makeText(this@SettingsActivity, "Spinner selected : ${parent.getItemAtPosition(position).toString()}", Toast.LENGTH_SHORT).show()
                PrefData(this@SettingsActivity).setCityPref("${parent.getItemAtPosition(position)}")
//                Toast.makeText(this, "Spinner selected : ${parent.getItemAtPosition(position).toString()}", Toast.LENGTH_SHORT)
//                text_view.text = "Spinner selected : ${parent.getItemAtPosition(position).toString()}"
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Another interface callback
            }
        }
    }
}
