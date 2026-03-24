package com.androidtv.tmdb.ui

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.androidtv.tmdb.R
import com.androidtv.tmdb.model.Video

class PlaybackActivity : FragmentActivity() {

    companion object {
        const val EXTRA_VIDEO = "extra_video"
        const val EXTRA_TITLE = "extra_title"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playback)
        if (savedInstanceState == null) {
            val video = intent.getSerializableExtra(EXTRA_VIDEO) as? Video
            val title = intent.getStringExtra(EXTRA_TITLE) ?: "Video"
            supportFragmentManager.beginTransaction()
                .replace(R.id.playback_frame, PlaybackFragment.newInstance(video, title))
                .commit()
        }
    }
}
