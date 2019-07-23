package com.reezkyillma.projectandroid

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView

import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.reezkyillma.projectandroid.Adapter.MessageAdapter
import com.reezkyillma.projectandroid.Fragments.ChatsFragment
import com.reezkyillma.projectandroid.Fragments.ProfileFragment
import com.reezkyillma.projectandroid.Fragments.UsersFragment
import com.reezkyillma.projectandroid.Model.Chat
import com.reezkyillma.projectandroid.Model.User

import java.util.ArrayList
import java.util.HashMap

import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_main__message.*

class MainMessage : AppCompatActivity() {

    lateinit var profile_image: CircleImageView
    lateinit var username: TextView

    internal var firebaseUser: FirebaseUser? = null
    lateinit var reference: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main__message)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setTitle("")

        profile_image = findViewById(R.id.profile_image)
        username = findViewById(R.id.username)

        firebaseUser = FirebaseAuth.getInstance().currentUser
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser!!.uid)


        newmessage.setOnClickListener {
            val intentMessage = Intent(this@MainMessage, UsersFragment::class.java)
            startActivity(intentMessage)
        }

        back.setOnClickListener {
            val intentMessage = Intent(this@MainMessage, MainActivity::class.java)
            startActivity(intentMessage)
        }


        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue<User>(User::class.java!!)
                username.setText(user!!.username)
                if (user!!.imageurl == "default") {
                    profile_image.setImageResource(R.mipmap.ic_launcher)
                } else {

                    //change this
                    Glide.with(applicationContext).load(user!!.imageurl).into(profile_image)
                }


            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })

//        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)
        val viewPager = findViewById<ViewPager>(R.id.view_pager)


        reference = FirebaseDatabase.getInstance().getReference("Chats")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
                var unread = 0
                for (snapshot in dataSnapshot.children) {
                    val chat = snapshot.getValue<Chat>(Chat::class.java!!)
                    if (chat!!.receiver == firebaseUser!!.uid && !chat!!.isIsseen) {
                        unread++
                    }
                }

                if (unread == 0) {
                    viewPagerAdapter.addFragment(ChatsFragment(), "Chats")
                } else {
                    viewPagerAdapter.addFragment(ChatsFragment(), "($unread) Chats")
                }


//                viewPagerAdapter.addFragment(UsersFragment(), "Users")
//                viewPagerAdapter.addFragment(ProfileFragment(), "Profile")

                viewPager.adapter = viewPagerAdapter


//                tabLayout.setupWithViewPager(viewPager)

            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })


    }


    internal inner class ViewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        private val fragments: ArrayList<Fragment>
        private val titles: ArrayList<String>

        init {
            this.fragments = ArrayList()
            this.titles = ArrayList()
        }

        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }

        override fun getCount(): Int {
            return fragments.size
        }

        fun addFragment(fragment: Fragment, title: String) {
            fragments.add(fragment)
            titles.add(title)
        }

        // Ctrl + O

        override fun getPageTitle(position: Int): CharSequence? {
            return titles[position]
        }
    }

    private fun status(status: String) {
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser!!.uid)

        val hashMap = HashMap<String, Any>()
        hashMap["status"] = status

        reference.updateChildren(hashMap)
    }

    override fun onResume() {
        super.onResume()
        status("online")
    }

    override fun onPause() {
        super.onPause()
        status("offline")
    }
}