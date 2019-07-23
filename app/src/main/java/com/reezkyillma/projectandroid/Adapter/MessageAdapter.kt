package com.reezkyillma.projectandroid.Adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.reezkyillma.projectandroid.Model.Chat
import com.reezkyillma.projectandroid.R

class MessageAdapter(private val mContext: Context, private val mChat: List<Chat>, private val imageurl: String) : RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    internal var fuser: FirebaseUser? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageAdapter.ViewHolder {
        if (viewType == MSG_TYPE_RIGHT) {
            val view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false)
            return ViewHolder(view)
        } else {
            val view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false)
            return ViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: MessageAdapter.ViewHolder, position: Int) {

        val chat = mChat[position]


        holder.show_message.text = chat.message
        if(chat.message!!.contains("https://firebasestorage")){
            holder.show_message.visibility = View.GONE
            Glide.with(mContext).load(chat.message).override(150,150)
                    .fitCenter()
                    .into(holder.img_message)
        }else{
            holder.img_message.visibility = View.GONE
            holder.show_message.text = chat.message
        }

        if (imageurl == "default") {
            holder.profile_image.setImageResource(R.mipmap.ic_launcher)
        } else {
            Glide.with(mContext).load(imageurl).error(R.mipmap.ic_launcher).into(holder.profile_image)
        }

        if (position == mChat.size - 1) {
            if (chat.isIsseen) {
                holder.txt_seen.text = "Seen"
            } else {
                holder.txt_seen.text = "Delivered"
            }
        } else {
            holder.txt_seen.visibility = View.GONE
        }


    }

    override fun getItemCount(): Int {
        return mChat.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var show_message: TextView
        var profile_image: ImageView
        var txt_seen: TextView
        var img_message: ImageView

        init {
            show_message = itemView.findViewById(R.id.show_message)
            profile_image = itemView.findViewById(R.id.profile_image)
            txt_seen = itemView.findViewById(R.id.txt_seen)
            img_message = itemView.findViewById(R.id.imageMessage)
        }
    }

    override fun getItemViewType(position: Int): Int {
        fuser = FirebaseAuth.getInstance().currentUser
        return if (mChat[position].sender == fuser!!.uid) {
            MSG_TYPE_RIGHT
        } else {
            MSG_TYPE_LEFT
        }
    }

    companion object {

        val MSG_TYPE_LEFT = 0
        val MSG_TYPE_RIGHT = 1
    }
}