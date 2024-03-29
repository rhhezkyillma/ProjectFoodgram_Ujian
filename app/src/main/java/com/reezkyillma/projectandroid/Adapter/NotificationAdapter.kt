package com.reezkyillma.projectandroid.Adapter


import android.content.Context
import android.content.SharedPreferences
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.reezkyillma.projectandroid.Fragments.PostDetailFragment
import com.reezkyillma.projectandroid.Fragments.ProfileFragment
import com.reezkyillma.projectandroid.Model.Notification
import com.reezkyillma.projectandroid.Model.Post
import com.reezkyillma.projectandroid.Model.User
import com.reezkyillma.projectandroid.R

import android.content.Context.MODE_PRIVATE

class NotificationAdapter(private val mContext: Context, private val mNotification: List<Notification>) : RecyclerView.Adapter<NotificationAdapter.ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationAdapter.ImageViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.notification_item, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationAdapter.ImageViewHolder, position: Int) {

        val notification = mNotification[position]

        holder.text.text = notification.text

        getUserInfo(holder.image_profile, holder.username, notification.userid)

        if (notification.isIspost) {
            holder.post_image.visibility = View.VISIBLE
            getPostImage(holder.post_image, notification.postid)
        } else {
            holder.post_image.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            if (notification.isIspost) {
                val editor = mContext.getSharedPreferences("PREFS", MODE_PRIVATE).edit()
                editor.putString("postid", notification.postid)
                editor.apply()

                (mContext as FragmentActivity).supportFragmentManager.beginTransaction().replace(R.id.fragment_container,
                        PostDetailFragment()).commit()
            } else {
                val editor = mContext.getSharedPreferences("PREFS", MODE_PRIVATE).edit()
                editor.putString("profileid", notification.userid)
                editor.apply()

                (mContext as FragmentActivity).supportFragmentManager.beginTransaction().replace(R.id.fragment_container,
                        ProfileFragment()).commit()
            }
        }


    }

    //
    override fun getItemCount(): Int {
        return mNotification.size
    }

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var image_profile: ImageView
        var post_image: ImageView
        var username: TextView
        var text: TextView

        init {

            image_profile = itemView.findViewById(R.id.image_profile)
            post_image = itemView.findViewById(R.id.post_image)
            username = itemView.findViewById(R.id.username)
            text = itemView.findViewById(R.id.comment)
        }
    }

    private fun getUserInfo(imageView: ImageView, username: TextView, publisherid: String?) {
        val reference = FirebaseDatabase.getInstance().reference
                .child("Users").child(publisherid!!)

        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(User::class.java)
                Glide.with(mContext).load(user!!.imageurl).into(imageView)
                username.text = user.username
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    private fun getPostImage(post_image: ImageView, postid: String?) {
        val reference = FirebaseDatabase.getInstance().reference
                .child("Posts").child(postid!!)

        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val post = dataSnapshot.getValue(Post::class.java)
                Glide.with(mContext).load(post!!.postimage).error(R.mipmap.ic_launcher).into(post_image)
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }
}