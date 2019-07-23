package com.reezkyillma.projectandroid.Fragments

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.reezkyillma.projectandroid.Adapter.MessageUserAdapter
import com.reezkyillma.projectandroid.MainActivity
import com.reezkyillma.projectandroid.MainMessage
import com.reezkyillma.projectandroid.Model.User
import com.reezkyillma.projectandroid.Model.UserChat
import com.reezkyillma.projectandroid.R
import kotlinx.android.synthetic.main.fragment_users.*

import java.util.ArrayList


class UsersFragment : AppCompatActivity() {

    private var recyclerView: RecyclerView? = null

    private var messageUserAdapter: MessageUserAdapter? = null
    private var mUsers: MutableList<UserChat>? = null

    lateinit var search_users: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.fragment_users)
        recyclerView = this.findViewById(R.id.recycler_view)
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager = LinearLayoutManager(this)

        mUsers = ArrayList()

        readUsers()

        search_users = findViewById(R.id.search_users)
        search_users.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                searchUsers(charSequence.toString().toLowerCase())
            }

            override fun afterTextChanged(editable: Editable) {

            }
        })

        back.setOnClickListener {
            val intentMessage = Intent(this@UsersFragment,MainMessage::class.java)
            startActivity(intentMessage)
        }

    }



    private fun searchUsers(s: String) {

//        GET ACTIVE USER
        val userItSelf = FirebaseAuth.getInstance().currentUser

//        GET SEARCHED USER
        val query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("username")
                .startAt(s)
                .endAt(s + "\uf8ff")

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                mUsers!!.clear()
                for (snapshot in dataSnapshot.children) {
                    val user = snapshot.getValue(UserChat::class.java)
                    if(user!!.id != userItSelf!!.uid){
                        user?.let { mUsers!!.add(it) }
                    }
                }
                messageUserAdapter!!.notifyDataSetChanged()
            }
            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }

    private fun readUsers() {

        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val reference = FirebaseDatabase.getInstance().getReference("Users")

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (search_users.text.toString() == "") {
                    mUsers!!.clear()
                    for (snapshot in dataSnapshot.children) {
                        val user = snapshot.getValue(UserChat::class.java)

                        if (user!!.id != firebaseUser!!.uid) {
                            mUsers!!.add(user)
                        }

                    }

                    messageUserAdapter = MessageUserAdapter(this@UsersFragment, mUsers!!, false)
                    val dividerItemDecoration = DividerItemDecoration(this@UsersFragment, DividerItemDecoration.VERTICAL)
                    dividerItemDecoration.setDrawable(resources.getDrawable(R.drawable.recyclerview_divider))
                    recyclerView!!.adapter = messageUserAdapter
                    recyclerView!!.addItemDecoration(dividerItemDecoration)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }
}
