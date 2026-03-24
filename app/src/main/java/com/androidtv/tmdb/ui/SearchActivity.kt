package com.androidtv.tmdb.ui

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.androidtv.tmdb.R

class SearchActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.search_frame, SearchFragment())
                .commit()
        }
    }
}
