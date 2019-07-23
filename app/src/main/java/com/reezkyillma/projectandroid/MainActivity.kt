package com.reezkyillma.projectandroid

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.reezkyillma.projectandroid.Fragments.*
import com.reezkyillma.projectandroid.Model.User
import com.reezkyillma.projectandroid.R.color.colorAccent
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.nav_header_main.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import com.reezkyillma.projectandroid.R.drawable.hujan as drawableHujan

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    internal var bottom_navigation: BottomNavigationView? = null
    internal var toolbar: android.support.v7.widget.Toolbar? = null
    internal var profileid: String? = null

    internal var selectedfragment: Fragment? = null

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar2)
        setSupportActionBar(toolbar)
        toolbar.setTitleTextColor(resources.getColor(R.color.white))

        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        val headerView: View = navigationView.getHeaderView(0)
        val nav_user_image: CircleImageView = headerView.findViewById<CircleImageView>(R.id.nav_profile_image)
        val nav_userName: TextView = headerView.findViewById<TextView>(R.id.nav_user_full_name)
        val nav_email: TextView = headerView.findViewById<TextView>(R.id.nav_user_email)
        nav_userName.text = ("Hi ... ")
        nav_email.text = ("email@domain.com")

        nav_user_image.setOnClickListener {
            View.OnClickListener {
                setTitle("")
                val editor = getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
                editor.putString("profileid", FirebaseAuth.getInstance().currentUser!!.uid)
                editor.apply()
                selectedfragment = ProfileFragment()
            }
        }


        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        navigationView.setNavigationItemSelectedListener(this)


        val intent = intent.extras
        if (intent != null) {
            val publisher = intent.getString("publisherid")

            val editor = getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
            editor.putString("profileid", publisher)
            editor.apply()

            supportFragmentManager.beginTransaction().replace(R.id.fragment_container,
                    WeatherFragment()).commit()
        } else {
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container,
                    WeatherFragment()).commit()
        }
        _getUserData()
    }


    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.nav_weather -> {

                selectedfragment = WeatherFragment()
                setTitle(null)
            }

            R.id.nav_home -> {
                selectedfragment = HomeFragment()
                setTitle("")
            }
            R.id.nav_search -> {

                selectedfragment = SearchFragment()
                setTitle("")
            }

            R.id.nav_message -> {
                selectedfragment = null
                setTitle("")
                startActivity(Intent(this@MainActivity, MainMessage::class.java))
            }

            R.id.nav_settings -> {
                selectedfragment = null
                setTitle("")
                startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
            }

            R.id.nav_logout -> {
                selectedfragment = null
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
//                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))
                finish()
            }

            R.id.nav_add -> {
                selectedfragment = null
                startActivity(Intent(this@MainActivity, PostActivity::class.java))
            }

            R.id.nav_recipe -> {
                setTitle("")
                selectedfragment = RecipeFragment()
            }
            R.id.nav_heart -> {
                setTitle("")
                selectedfragment = NotificationFragment()
            }
            R.id.nav_profile -> {
                setTitle("")
                val editor = getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
                editor.putString("profileid", FirebaseAuth.getInstance().currentUser!!.uid)
                editor.apply()
                selectedfragment = ProfileFragment()
            }
        }
        if (selectedfragment != null) {
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container,
                    selectedfragment!!).commit()
        }
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    private fun _getUserData() {
        val activeUser = FirebaseAuth.getInstance().currentUser
        println(activeUser)
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        val headerView: View = navigationView.getHeaderView(0)
        val nav_user_image = headerView.findViewById<CircleImageView>(R.id.nav_profile_image)
        val nav_userName = headerView.findViewById<TextView>(R.id.nav_user_full_name)
        val nav_userEmail = headerView.findViewById<TextView>(R.id.nav_user_email)

        val reference = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(activeUser!!.uid)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                println(error)
            }

            override fun onDataChange(p0: DataSnapshot) {
                val user = p0.getValue(User::class.java)
                println(user!!.imageurl)
                println(user.username)

                Glide.with(applicationContext)
                        .load(user.imageurl)
                        .error(R.mipmap.ic_launcher)
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .into(nav_user_image)
                nav_userName.text = user.username
                nav_userEmail.text = user.fullname
            }

        })
    }

    override fun onBackPressed() {
        val count = supportFragmentManager.backStackEntryCount

        if (count == 0) {
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
        } else {
            val intentback = Intent(this, MainActivity::class.java)
            startActivity(intentback)
        }
    }

}
