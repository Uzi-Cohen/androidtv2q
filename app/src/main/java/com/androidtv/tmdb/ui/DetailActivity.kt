package com.androidtv.tmdb.ui

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.androidtv.tmdb.R

class DetailActivity : FragmentActivity() {

    companion object {
        const val EXTRA_MEDIA_ID = "media_id"
        const val EXTRA_MEDIA_TYPE = "media_type" // "movie" or "tv"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        if (savedInstanceState == null) {
            val mediaId = intent.getLongExtra(EXTRA_MEDIA_ID, -1)
            val mediaType = intent.getStringExtra(EXTRA_MEDIA_TYPE) ?: "movie"
            supportFragmentManager.beginTransaction()
                .replace(R.id.detail_frame, DetailFragment.newInstance(mediaId, mediaType))
                .commit()
        }
    }
}
