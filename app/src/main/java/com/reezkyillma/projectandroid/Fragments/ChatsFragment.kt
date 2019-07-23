package com.reezkyillma.projectandroid.Fragments


import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.iid.FirebaseInstanceId
import com.reezkyillma.projectandroid.Adapter.MessageUserAdapter
import com.reezkyillma.projectandroid.Model.Chatlist
import com.reezkyillma.projectandroid.Model.User
import com.reezkyillma.projectandroid.Model.UserChat
import com.reezkyillma.projectandroid.Notifications.Token
import com.reezkyillma.projectandroid.R

import java.util.ArrayList


class ChatsFragment : Fragment() {

    private var recyclerView: RecyclerView? = null

    private var messageUserAdapter: MessageUserAdapter? = null
    private var mUsers: MutableList<UserChat>? = null

    internal var fuser: FirebaseUser? = null
    internal lateinit var reference: DatabaseReference

    private var usersList: MutableList<Chatlist>? = null

    override fun onResume() {
        super.onResume()
        _initialize()
    }

    fun _initialize() {



        fuser = FirebaseAuth.getInstance().currentUser

        usersList = ArrayList()

        reference = FirebaseDatabase.getInstance().getReference("Chatlist").child(fuser!!.uid)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                usersList!!.clear()
                for (snapshot in dataSnapshot.children) {
                    val chatlist = snapshot.getValue(Chatlist::class.java)
                    usersList!!.add(chatlist!!)
                }

                chatList()
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })

        updateToken(FirebaseInstanceId.getInstance().token)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_chats, container, false)
        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager = LinearLayoutManager(context)

        _initialize()
        return view
    }

    private fun updateToken(token: String?) {
        val reference = FirebaseDatabase.getInstance().getReference("Tokens")
        val token1 = Token(token!!)
        reference.child(fuser!!.uid).setValue(token1)
    }

    private fun chatList() {
        mUsers = ArrayList()
        reference = FirebaseDatabase.getInstance().getReference("Users")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                mUsers!!.clear()
                for (snapshot in dataSnapshot.children) {
                    val user = snapshot.getValue(UserChat::class.java)
                    for (chatlist in usersList!!) {
                        if (user!!.id == chatlist.id) {
                            mUsers!!.add(user)
                        }
                    }
                }
                if (mUsers != null) {
                    messageUserAdapter = context?.let { MessageUserAdapter(it, mUsers!!, true) }
                    val dividerItemDecoration = DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
                    dividerItemDecoration.setDrawable(resources.getDrawable(R.drawable.recyclerview_divider))
                    recyclerView?.addItemDecoration(dividerItemDecoration)
                    recyclerView?.adapter = messageUserAdapter
                }else{
                    showDialogInternet()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    fun showDialogInternet() {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Network Error")
                .setMessage("Please check your internet connection and try again")
                .setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
                .setCancelable(true)
        builder.show()
    }

}
