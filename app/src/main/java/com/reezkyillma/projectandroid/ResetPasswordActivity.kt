package com.reezkyillma.projectandroid


import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.reezkyillma.projectandroid.LoginActivity
import com.reezkyillma.projectandroid.R

class ResetPasswordActivity : AppCompatActivity() {

    lateinit var send_email: EditText
    lateinit var btn_reset: Button

    lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)
//
//        val toolbar = findViewById<Toolbar>(R.id.toolbar)
//        setSupportActionBar(toolbar)
//        supportActionBar!!.setTitle("Reset Password")
//        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        send_email = findViewById(R.id.send_email)
        btn_reset = findViewById(R.id.btn_reset)

        firebaseAuth = FirebaseAuth.getInstance()

        btn_reset.setOnClickListener {
            val email = send_email.text.toString()

            if (email == "") {
                Toast.makeText(this@ResetPasswordActivity, "All fileds are required!", Toast.LENGTH_SHORT).show()
            } else {
                firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this@ResetPasswordActivity, "Please check you Email", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@ResetPasswordActivity, LoginActivity::class.java))
                    } else {
                        val error = task.exception!!.message
                        Toast.makeText(this@ResetPasswordActivity, error, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    }
}
