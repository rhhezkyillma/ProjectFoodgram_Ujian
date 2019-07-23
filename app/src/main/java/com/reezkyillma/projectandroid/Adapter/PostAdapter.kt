package com.reezkyillma.projectandroid.Adapter

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast

import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.reezkyillma.projectandroid.CommentsActivity
import com.reezkyillma.projectandroid.FollowersActivity
import com.reezkyillma.projectandroid.Fragments.PostDetailFragment
import com.reezkyillma.projectandroid.Fragments.ProfileFragment
import com.reezkyillma.projectandroid.Model.Post
import com.reezkyillma.projectandroid.Model.User
import com.reezkyillma.projectandroid.R

import java.util.HashMap

import android.content.Context.MODE_PRIVATE

class PostAdapter(private val mContext: Context, private val mPosts: List<Post>) : RecyclerView.Adapter<PostAdapter.ImageViewHolder>() {

    private var firebaseUser: FirebaseUser? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostAdapter.ImageViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.post_item, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostAdapter.ImageViewHolder, position: Int) {

        firebaseUser = FirebaseAuth.getInstance().currentUser
        val post = mPosts[position]

        Glide.with(mContext).load(post.postimage)
                .apply(RequestOptions().placeholder(R.drawable.placeholder))
                .into(holder.post_image)

        if (post.description == "") {
            holder.description.visibility = View.GONE
        } else {
            holder.description.visibility = View.VISIBLE
            holder.description.text = post.description
        }

        publisherInfo(holder.image_profile, holder.username, holder.publisher, post.publisher)
        isLiked(post.postid, holder.like)
        isSaved(post.postid, holder.save)
        nrLikes(holder.likes, post.postid)
        getCommetns(post.postid, holder.comments)

        holder.like.setOnClickListener {
            if (holder.like.tag == "like") {
                FirebaseDatabase.getInstance().reference.child("Likes").child(post.postid!!)
                        .child(firebaseUser!!.uid).setValue(true)
                addNotification(post.publisher, post.postid)
            } else {
                FirebaseDatabase.getInstance().reference.child("Likes").child(post.postid!!)
                        .child(firebaseUser!!.uid).removeValue()
            }
        }

        holder.save.setOnClickListener {
            if (holder.save.tag == "save") {
                FirebaseDatabase.getInstance().reference.child("Saves").child(firebaseUser!!.uid)
                        .child(post.postid!!).setValue(true)
            } else {
                FirebaseDatabase.getInstance().reference.child("Saves").child(firebaseUser!!.uid)
                        .child(post.postid!!).removeValue()
            }
        }

        holder.image_profile.setOnClickListener {
            val editor = mContext.getSharedPreferences("PREFS", MODE_PRIVATE).edit()
            editor.putString("profileid", post.publisher)
            editor.apply()

            (mContext as FragmentActivity).supportFragmentManager.beginTransaction().replace(R.id.fragment_container,
                    ProfileFragment()).commit()
        }

        holder.username.setOnClickListener {
            val editor = mContext.getSharedPreferences("PREFS", MODE_PRIVATE).edit()
            editor.putString("profileid", post.publisher)
            editor.apply()

            (mContext as FragmentActivity).supportFragmentManager.beginTransaction().replace(R.id.fragment_container,
                    ProfileFragment()).commit()
        }

        holder.publisher.setOnClickListener {
            val editor = mContext.getSharedPreferences("PREFS", MODE_PRIVATE).edit()
            editor.putString("profileid", post.publisher)
            editor.apply()

            (mContext as FragmentActivity).supportFragmentManager.beginTransaction().replace(R.id.fragment_container,
                    ProfileFragment()).commit()
        }

        holder.comment.setOnClickListener {
            val intent = Intent(mContext, CommentsActivity::class.java)
            intent.putExtra("postid", post.postid)
            intent.putExtra("publisherid", post.publisher)
            mContext.startActivity(intent)
        }

        holder.comments.setOnClickListener {
            val intent = Intent(mContext, CommentsActivity::class.java)
            intent.putExtra("postid", post.postid)
            intent.putExtra("publisherid", post.publisher)
            mContext.startActivity(intent)
        }

        holder.post_image.setOnClickListener {
            val editor = mContext.getSharedPreferences("PREFS", MODE_PRIVATE).edit()
            editor.putString("postid", post.postid)
            editor.apply()

            (mContext as FragmentActivity).supportFragmentManager.beginTransaction().replace(R.id.fragment_container,
                    PostDetailFragment()).commit()
        }

        holder.likes.setOnClickListener {
            val intent = Intent(mContext, FollowersActivity::class.java)
            intent.putExtra("id", post.postid)
            intent.putExtra("title", "likes")
            mContext.startActivity(intent)
        }

        holder.more.setOnClickListener { view ->
            val popupMenu = PopupMenu(mContext, view)
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.edit -> {
                        editPost(post.postid)
                        true
                    }
                    R.id.delete -> {
                        val id = post.postid
                        FirebaseDatabase.getInstance().getReference("Posts")
                                .child(post.postid!!).removeValue()
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        deleteNotifications(id, firebaseUser!!.uid)
                                    }
                                }
                        true
                    }
                    R.id.report -> {
                        Toast.makeText(mContext, "Reported clicked!", Toast.LENGTH_SHORT).show()
                        true
                    }
                    else -> false
                }
            }
            popupMenu.inflate(R.menu.post_menu)
            if (post.publisher != firebaseUser!!.uid) {
                popupMenu.menu.findItem(R.id.edit).isVisible = false
                popupMenu.menu.findItem(R.id.delete).isVisible = false
            }
            popupMenu.show()
        }
    }

    override fun getItemCount(): Int {
        return mPosts.size
    }

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var image_profile: ImageView
        var post_image: ImageView
        var like: ImageView
        var comment: ImageView
        var save: ImageView
        var more: ImageView
        var username: TextView
        var likes: TextView
        var publisher: TextView
        var description: TextView
        var comments: TextView

        init {

            image_profile = itemView.findViewById(R.id.image_profile)
            username = itemView.findViewById(R.id.username)
            post_image = itemView.findViewById(R.id.post_image)
            like = itemView.findViewById(R.id.like)
            comment = itemView.findViewById(R.id.comment)
            save = itemView.findViewById(R.id.save)
            likes = itemView.findViewById(R.id.likes)
            publisher = itemView.findViewById(R.id.publisher)
            description = itemView.findViewById(R.id.description)
            comments = itemView.findViewById(R.id.comments)
            more = itemView.findViewById(R.id.more)
        }
    }

    private fun addNotification(userid: String?, postid: String?) {
        val reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userid!!)

        val hashMap = HashMap<String, Any>()
        hashMap["userid"] = firebaseUser!!.uid
        hashMap["text"] = "liked your post"
        hashMap["postid"] = postid.toString()
        hashMap["ispost"] = true

        reference.push().setValue(hashMap)
    }

    private fun deleteNotifications(postid: String?, userid: String) {
        val reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userid)
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    if (snapshot.child("postid").value == postid) {
                        snapshot.ref.removeValue()
                                .addOnCompleteListener { Toast.makeText(mContext, "Deleted!", Toast.LENGTH_SHORT).show() }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    private fun nrLikes(likes: TextView, postId: String?) {
        val reference = FirebaseDatabase.getInstance().reference.child("Likes").child(postId!!)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                likes.text = dataSnapshot.childrenCount.toString() + " likes"
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })

    }

    private fun getCommetns(postId: String?, comments: TextView) {
        val reference = FirebaseDatabase.getInstance().reference.child("Comments").child(postId!!)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                comments.text = "View All " + dataSnapshot.childrenCount + " Comments"
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    private fun publisherInfo(image_profile: ImageView, username: TextView, publisher: TextView, userid: String?) {
        val reference = FirebaseDatabase.getInstance().reference
                .child("Users").child(userid!!)

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(User::class.java)
                Glide.with(mContext).load(user!!.imageurl).error(R.mipmap.ic_launcher).into(image_profile)
                username.text = user.username
                publisher.text = user.username
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    private fun isLiked(postid: String?, imageView: ImageView) {

        val firebaseUser = FirebaseAuth.getInstance().currentUser

        val reference = FirebaseDatabase.getInstance().reference
                .child("Likes").child(postid!!)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.child(firebaseUser!!.uid).exists()) {
                    imageView.setImageResource(R.drawable.ic_liked)
                    imageView.tag = "liked"
                } else {
                    imageView.setImageResource(R.drawable.ic_like)
                    imageView.tag = "like"
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    private fun isSaved(postid: String?, imageView: ImageView) {

        val firebaseUser = FirebaseAuth.getInstance().currentUser

        val reference = FirebaseDatabase.getInstance().reference
                .child("Saves").child(firebaseUser!!.uid)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.child(postid!!).exists()) {
                    imageView.setImageResource(R.drawable.ic_save_black)
                    imageView.tag = "saved"
                } else {
                    imageView.setImageResource(R.drawable.ic_savee_black)
                    imageView.tag = "save"
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    private fun editPost(postid: String?) {
        val alertDialog = AlertDialog.Builder(mContext)
        alertDialog.setTitle("Edit Post")

        val editText = EditText(mContext)
        val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT)
        editText.layoutParams = lp
        alertDialog.setView(editText)

        getText(postid, editText)

        alertDialog.setPositiveButton("Edit"
        ) { dialogInterface, i ->
            val hashMap = HashMap<String, Any>()
            hashMap["description"] = editText.text.toString()

            FirebaseDatabase.getInstance().getReference("Posts")
                    .child(postid!!).updateChildren(hashMap)
        }
        alertDialog.setNegativeButton("Cancel"
        ) { dialogInterface, i -> dialogInterface.cancel() }
        alertDialog.show()
    }

    private fun getText(postid: String?, editText: EditText) {
        val reference = FirebaseDatabase.getInstance().getReference("Posts")
                .child(postid!!)
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                editText.setText(dataSnapshot.getValue(Post::class.java)!!.description)
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }
}