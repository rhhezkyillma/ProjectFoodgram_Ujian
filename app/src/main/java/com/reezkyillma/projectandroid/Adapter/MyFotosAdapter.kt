package com.reezkyillma.projectandroid.Adapter

import android.content.Context
import android.content.SharedPreferences
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

import com.bumptech.glide.Glide
import com.reezkyillma.projectandroid.Fragments.PostDetailFragment
import com.reezkyillma.projectandroid.Model.Post
import com.reezkyillma.projectandroid.R

import android.content.Context.MODE_PRIVATE

class MyFotosAdapter(private val mContext: Context, private val mPosts: List<Post>) : RecyclerView.Adapter<MyFotosAdapter.ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyFotosAdapter.ImageViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.fotos_item, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyFotosAdapter.ImageViewHolder, position: Int) {

        val post = mPosts[position]

        Glide.with(mContext).load(post.postimage).into(holder.post_image)

        holder.post_image.setOnClickListener {
            val editor = mContext.getSharedPreferences("PREFS", MODE_PRIVATE).edit()
            editor.putString("postid", post.postid)
            editor.apply()

            (mContext as FragmentActivity).supportFragmentManager.beginTransaction().replace(R.id.fragment_container,
                    PostDetailFragment()).commit()
        }

    }

    override fun getItemCount(): Int {
        return mPosts.size
    }

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var post_image: ImageView


        init {

            post_image = itemView.findViewById(R.id.post_image)

        }
    }
}
