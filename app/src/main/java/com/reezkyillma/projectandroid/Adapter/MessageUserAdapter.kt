package com.reezkyillma.projectandroid.Adapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.reezkyillma.projectandroid.Fragments.UsersFragment
import com.reezkyillma.projectandroid.MainActivity
import com.reezkyillma.projectandroid.MainMessage
import com.reezkyillma.projectandroid.MessageActivity
import com.reezkyillma.projectandroid.Model.Chat
import com.reezkyillma.projectandroid.Model.User
import com.reezkyillma.projectandroid.Model.UserChat
import com.reezkyillma.projectandroid.R

class MessageUserAdapter(private val mContext: Context, private val mUsers: List<UserChat>, private val ischat: Boolean) : RecyclerView.Adapter<MessageUserAdapter.ViewHolder>() {

    internal var theLastMessage: String? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.message_user_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val user = mUsers[position]
        holder.username.text = user.username
        if (user.imageurl == "default") {
            holder.profile_image.setImageResource(R.mipmap.ic_launcher)
        } else {
            Glide.with(mContext).load(user.imageurl).error(R.mipmap.ic_launcher).into(holder.profile_image)
        }

        if (ischat) {
            lastMessage(user.id, holder.last_msg)
        } else {
            holder.last_msg.visibility = View.GONE
        }

        if (ischat) {
            if (user.status == "online") {
                holder.img_on.visibility = View.VISIBLE
                holder.img_off.visibility = View.GONE
            } else {
                holder.img_on.visibility = View.GONE
                holder.img_off.visibility = View.VISIBLE
            }
        } else {
            holder.img_on.visibility = View.GONE
            holder.img_off.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(mContext, MessageActivity::class.java)
            intent.putExtra("userid", user.id)
            mContext.startActivity(intent)
        }


        holder.itemView.setOnLongClickListener {

            showDialog("", user.id.toString(), user.username.toString())
            return@setOnLongClickListener true
        }
    }

    fun showDialog(idChatList: String, idTarget: String, targetName: String) {
        val activeUser = FirebaseAuth.getInstance().currentUser
        val idActiveUser = activeUser!!.uid
        val dbChatTarget = FirebaseDatabase
                .getInstance()
                .getReference("Chatlist")
                .child(idActiveUser.toString())
                .child(idTarget)


        val builder = AlertDialog.Builder(mContext)
        builder.setTitle("Delete Message")
        builder.setMessage("Delete Chat with $targetName ?")
        builder.setPositiveButton("Yes") { builder, which ->
            dbChatTarget.removeValue()
            builder.dismiss()
            Toast.makeText(mContext, "Chat with $targetName has been deleted !", Toast.LENGTH_LONG).show()
        }
        builder.setNegativeButton("No") { builder, which ->
            builder.dismiss()
        }
        builder.setCancelable(true)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    override fun getItemCount(): Int {
        return mUsers.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var username: TextView
        var profile_image: ImageView
        val img_on: ImageView
        val img_off: ImageView
        val last_msg: TextView

        init {

            username = itemView.findViewById(R.id.username)
            profile_image = itemView.findViewById(R.id.profile_image)
            img_on = itemView.findViewById(R.id.img_on)
            img_off = itemView.findViewById(R.id.img_off)
            last_msg = itemView.findViewById(R.id.last_msg)
        }
    }

    //check for last message
    private fun lastMessage(userid: String?, last_msg: TextView) {
        theLastMessage = "default"
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val reference = FirebaseDatabase.getInstance().getReference("Chats")

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val chat = snapshot.getValue(Chat::class.java)
                    if (firebaseUser != null && chat != null) {
                        if (chat.receiver == firebaseUser.uid && chat.sender == userid || chat.receiver == userid && chat.sender == firebaseUser.uid) {
                            theLastMessage = chat.message
                            println("LAST MESSAGE =>" + theLastMessage)
                        }
                    }
                }

                when (theLastMessage) {

                    "default" -> last_msg.text = "No Message"

                    else -> last_msg.text = theLastMessage
                }

                theLastMessage = "default"
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }
}

