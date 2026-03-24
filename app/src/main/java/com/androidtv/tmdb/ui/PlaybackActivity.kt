package com.androidtv.tmdb.ui

import android.app.Activity
import android.os.Bundle
import android.view.KeyEvent
import com.androidtv.tmdb.R
import com.bitmovin.analytics.api.AnalyticsConfig
import com.bitmovin.analytics.api.SourceMetadata
import com.bitmovin.player.PlayerView
import com.bitmovin.player.api.Player
import com.bitmovin.player.api.analytics.AnalyticsPlayerConfig
import com.bitmovin.player.api.analytics.AnalyticsSourceConfig
import com.bitmovin.player.api.source.Source
import com.bitmovin.player.api.source.SourceConfig
import com.bitmovin.player.api.source.SourceType

class PlaybackActivity : Activity() {

    companion object {
        const val EXTRA_TITLE = "extra_title"
    }

    private var analyticsLicenseKey = "e8501282-73fc-4df8-9922-1a7ef817cb78"
    private lateinit var playerView: PlayerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playback)

        val analyticsConfig = AnalyticsConfig(
            licenseKey = analyticsLicenseKey,
        )
        val player = Player(
            context = this,
            analyticsConfig = AnalyticsPlayerConfig.Enabled(analyticsConfig),
        )
        playerView = findViewById(R.id.playerView)
        playerView.player = player

        val streamTitle = intent.getStringExtra(EXTRA_TITLE) ?: "Art of Motion"
        val source = Source(
            SourceConfig(
                url = "https://cdn.bitmovin.com/content/assets/art-of-motion-dash-hls-progressive/mpds/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.mpd",
                type = SourceType.Dash,
                title = streamTitle,
            ),
            AnalyticsSourceConfig.Enabled(
                SourceMetadata(
                    videoId = "androidtv-wizard-Art_of_Motion-1774359529041",
                    title = streamTitle,
                )
            ),
        )

        player.load(source)
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN) {
            if (handleUserInput(event.keyCode)) {
                return true
            }
        }
        return super.dispatchKeyEvent(event)
    }

    private fun handleUserInput(keycode: Int): Boolean {
        val seekingOffsetSeconds = 15
        val player = playerView.player ?: return false
        return when (keycode) {
            KeyEvent.KEYCODE_DPAD_CENTER,
            KeyEvent.KEYCODE_ENTER,
            KeyEvent.KEYCODE_NUMPAD_ENTER,
            KeyEvent.KEYCODE_SPACE,
            KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> {
                if (player.isPlaying) player.pause() else player.play()
                true
            }
            KeyEvent.KEYCODE_MEDIA_PLAY -> {
                player.play()
                true
            }
            KeyEvent.KEYCODE_MEDIA_PAUSE -> {
                player.pause()
                true
            }
            KeyEvent.KEYCODE_MEDIA_STOP -> {
                player.pause()
                player.seek(0.0)
                true
            }
            KeyEvent.KEYCODE_DPAD_RIGHT,
            KeyEvent.KEYCODE_MEDIA_FAST_FORWARD -> {
                player.seek(player.currentTime + seekingOffsetSeconds)
                true
            }
            KeyEvent.KEYCODE_DPAD_LEFT,
            KeyEvent.KEYCODE_MEDIA_REWIND -> {
                player.seek(player.currentTime - seekingOffsetSeconds)
                true
            }
            else -> false
        }
    }

    override fun onStart() {
        super.onStart()
        playerView.onStart()
    }

    override fun onResume() {
        super.onResume()
        playerView.onResume()
    }

    override fun onPause() {
        super.onPause()
        playerView.onPause()
    }

    override fun onStop() {
        super.onStop()
        playerView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        playerView.onDestroy()
    }
}
