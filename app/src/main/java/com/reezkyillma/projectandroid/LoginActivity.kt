package com.reezkyillma.projectandroid

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main__message.*

class  LoginActivity : AppCompatActivity() {

    internal lateinit var email: EditText
    internal lateinit var password: EditText
    internal lateinit var login: Button
    internal lateinit var txt_signup: TextView
    internal lateinit var txt_forgot: TextView

    internal var firebaseUser: FirebaseUser? = null

    internal lateinit var auth: FirebaseAuth
    override fun onStart() {
        super.onStart()
        firebaseUser = FirebaseAuth.getInstance().currentUser

        if (firebaseUser != null) {
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        email = findViewById(R.id.email)
        password = findViewById(R.id.password)

        email.clearFocus()
        password.clearFocus()

        login = findViewById(R.id.login)
        txt_signup = findViewById(R.id.txt_signup)
        txt_forgot = findViewById(R.id.txt_forgot)

        auth = FirebaseAuth.getInstance()

        txt_signup.setOnClickListener { startActivity(Intent(this@LoginActivity, RegisterActivity::class.java)) }

//        txt_forgot.setOnClickListener {
//            startActivity(Intent(this@LoginActivity, ResetPasswordActivity::class.java)) }

        txt_forgot.setOnClickListener {
            val intentMessage = Intent(this@LoginActivity, ResetPasswordActivity::class.java)
            startActivity(intentMessage)
        }

        login.setOnClickListener {
//            val pd = ProgressDialog(this@LoginActivity)
//            pd.setMessage("Please wait...")
//            pd.show()

            val str_email = email.text.toString()
            val str_password = password.text.toString()
            //CHECK THE CONNECTION ON DEVICE
            val connectivityManager = this@LoginActivity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
            val isConnectivityManager: Boolean = activeNetwork?.isConnected == true

            if (TextUtils.isEmpty(str_email) || TextUtils.isEmpty(str_password)) {
                Toast.makeText(this@LoginActivity, "All fields are required!", Toast.LENGTH_SHORT).show()
            } else if (isConnectivityManager) {
//                val pd = ProgressDialog(this@LoginActivity)
//                pd.setMessage("Please wait...")
//                pd.show()
                auth.signInWithEmailAndPassword(str_email, str_password)
                        .addOnCompleteListener(this@LoginActivity) { task ->
                            if (task.isSuccessful) {

                                val reference = FirebaseDatabase.getInstance().reference.child("Users")
                                        .child(auth.currentUser!!.uid)

                                reference.addValueEventListener(object : ValueEventListener {
                                    override fun onDataChange(dataSnapshot: DataSnapshot) {
//                                        pd.dismiss()
                                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                        startActivity(intent)
                                        finish()
                                    }

                                    override fun onCancelled(databaseError: DatabaseError) {
//                                        pd.dismiss()
                                    }
                                })
                            } else {
//                                pd.dismiss()
                                Toast.makeText(this@LoginActivity, "Username salah", Toast.LENGTH_SHORT).show()
                            }
                        }
            }
            else{
                showDialogInternet()
            }
        }
    }

    override fun onBackPressed() {
//        super.onBackPressed()
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Exit Foodgram")
                .setMessage("Are you sure wants to exit Foodgram ?")
                .setPositiveButton("YES", DialogInterface.OnClickListener { dialog, which ->
                    dialog.dismiss()
                    super.onBackPressed()
                })
                .setNegativeButton("NO", DialogInterface.OnClickListener { dialog, which ->
                    dialog.dismiss()
                })
                .setCancelable(true)
        builder.show()
    }

    fun showDialogInternet() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Network Error")
                .setMessage("Please check your internet connection and try again")
                .setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
                .setCancelable(true)
        builder.show()
    }
}