package com.reezkyillma.projectandroid

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.text.Layout.JUSTIFICATION_MODE_INTER_WORD
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayer.OnInitializedListener
import com.google.android.youtube.player.YouTubePlayerView
import kotlinx.android.synthetic.main.activity_detail_menu.*
import kotlinx.android.synthetic.main.content_detail_menu.*

class DetailMenuActivity : YouTubeBaseActivity() {



    companion object {
        val YOUTUBE_API_KEY: String = R.string.YOUTUBE_API_KEY.toString()

    }

    lateinit var YoutubePlayerInit: YouTubePlayer.OnInitializedListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_menu)
//        setSupportActionBar(toolbar)

        foodLoc.setOnClickListener {
            val bundleData = intent.extras
            val name = bundleData.getString("NAME")
            val gmmIntentUri = Uri.parse("geo:0,0?q=$name")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            startActivity(mapIntent)
        }

        _initialize()

    }

    fun _initialize() {
        val bundleData = intent.extras

        Glide.with(this)
                .load(bundleData.getString("IMAGE_URL"))
                .placeholder(resources.getDrawable(R.drawable.placeholder))
                .into(headerImage)
        foodName.text = bundleData.getString("NAME")
        foodDesc.text = bundleData.getString("DESC")

//        foodDesc.setJustificationMode(JUSTIFICATION_MODE_INTER_WORD);
//        toolbar.title = bundleData.getString("NAME")
        toolbar_layout.title = bundleData.getString("NAME")
        _loadVideo(bundleData.getString("VIDEO_URL"))

    }

    private fun _loadVideo(videoId: String) {
        YoutubePlayerInit = object : OnInitializedListener {
            override fun onInitializationSuccess(p0: YouTubePlayer.Provider?, p1: YouTubePlayer?, p2: Boolean) {
//                p1?.loadVideo(videoId)
                p1?.cueVideo(videoId)
            }

            override fun onInitializationFailure(p0: YouTubePlayer.Provider?, p1: YouTubeInitializationResult?) {
                Toast.makeText(this@DetailMenuActivity, "Oops ! Terjadi kesalahan",Toast.LENGTH_SHORT).show()
            }
        }

        youtubePlayer.initialize(YOUTUBE_API_KEY,YoutubePlayerInit)
    }
}
