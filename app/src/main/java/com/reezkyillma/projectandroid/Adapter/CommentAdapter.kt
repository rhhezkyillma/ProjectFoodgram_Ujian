package com.reezkyillma.projectandroid.Adapter


import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.reezkyillma.projectandroid.MainActivity
import com.reezkyillma.projectandroid.Model.Comment
import com.reezkyillma.projectandroid.Model.User
import com.reezkyillma.projectandroid.R

class CommentAdapter(private val mContext: Context, private val mComment: List<Comment>, private val postid: String) : RecyclerView.Adapter<CommentAdapter.ImageViewHolder>() {

    private var firebaseUser: FirebaseUser? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentAdapter.ImageViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.comment_item, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentAdapter.ImageViewHolder, position: Int) {

        firebaseUser = FirebaseAuth.getInstance().currentUser
        val comment = mComment[position]

        holder.comment.text = comment.comment
        getUserInfo(holder.image_profile, holder.username, comment.publisher)

        holder.username.setOnClickListener {
            val intent = Intent(mContext, MainActivity::class.java)
            intent.putExtra("publisherid", comment.publisher)
            mContext.startActivity(intent)
        }

        holder.image_profile.setOnClickListener {
            val intent = Intent(mContext, MainActivity::class.java)
            intent.putExtra("publisherid", comment.publisher)
            mContext.startActivity(intent)
        }

        holder.itemView.setOnLongClickListener {
            if (comment.publisher == firebaseUser!!.uid) {

                val alertDialog = AlertDialog.Builder(mContext).create()
                alertDialog.setTitle("Do you want to delete?")
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "No"
                ) { dialog, which -> dialog.dismiss() }
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes"
                ) { dialog, which ->
                    FirebaseDatabase.getInstance().getReference("Comments")
                            .child(postid).child(comment.commentid!!)
                            .removeValue().addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(mContext, "Deleted!", Toast.LENGTH_SHORT).show()
                                }
                            }
                    dialog.dismiss()
                }
                alertDialog.show()
            }
            true
        }

    }

    override fun getItemCount(): Int {
        return mComment.size
    }

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var image_profile: ImageView
        var username: TextView
        var comment: TextView

        init {

            image_profile = itemView.findViewById(R.id.image_profile)
            username = itemView.findViewById(R.id.username)
            comment = itemView.findViewById(R.id.comment)
        }
    }

    private fun getUserInfo(imageView: ImageView, username: TextView, publisherid: String?) {
        val reference = FirebaseDatabase.getInstance().reference
                .child("Users").child(publisherid!!)

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(User::class.java)
                Glide.with(mContext).load(user!!.imageurl).error(R.mipmap.ic_launcher).into(imageView)
                username.text = user.username
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }
}
