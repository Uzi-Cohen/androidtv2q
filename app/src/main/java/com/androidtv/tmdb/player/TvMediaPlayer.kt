package com.androidtv.tmdb.player

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.KeyEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View

/**
 * Wrapper around Android's MediaPlayer for TV playback.
 * Handles surface lifecycle, D-pad controls, and state management.
 */
class TvMediaPlayer(
    private val context: Context,
    private val surfaceView: SurfaceView,
    private val onPrepared: () -> Unit = {},
    private val onError: (what: Int, extra: Int) -> Unit = { _, _ -> },
    private val onCompletion: () -> Unit = {},
    private val onProgressUpdate: (currentPosition: Int, duration: Int) -> Unit = { _, _ -> }
) {

    companion object {
        private const val TAG = "TvMediaPlayer"
        private const val SEEK_INCREMENT_MS = 10_000 // 10 seconds
        private const val PROGRESS_UPDATE_INTERVAL_MS = 1000L
    }

    private var mediaPlayer: MediaPlayer? = null
    private var isPrepared = false
    private var pendingUrl: String? = null
    private val handler = Handler(Looper.getMainLooper())

    private val progressRunnable = object : Runnable {
        override fun run() {
            mediaPlayer?.let { mp ->
                if (isPrepared && mp.isPlaying) {
                    onProgressUpdate(mp.currentPosition, mp.duration)
                }
            }
            handler.postDelayed(this, PROGRESS_UPDATE_INTERVAL_MS)
        }
    }

    private val surfaceCallback = object : SurfaceHolder.Callback {
        override fun surfaceCreated(holder: SurfaceHolder) {
            mediaPlayer?.setDisplay(holder)
            pendingUrl?.let { url ->
                pendingUrl = null
                prepareAndPlay(url)
            }
        }

        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            // Adjust video scaling if needed
        }

        override fun surfaceDestroyed(holder: SurfaceHolder) {
            mediaPlayer?.setDisplay(null)
        }
    }

    init {
        surfaceView.holder.addCallback(surfaceCallback)
        setupDpadControls()
    }

    fun play(url: String, title: String = "") {
        Log.d(TAG, "Play requested: $title ($url)")
        release()

        if (surfaceView.holder.surface.isValid) {
            prepareAndPlay(url)
        } else {
            pendingUrl = url
        }
    }

    private fun prepareAndPlay(url: String) {
        try {
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MOVIE)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )

                setDisplay(surfaceView.holder)
                setDataSource(url)
                setScreenOnWhilePlaying(true)

                setOnPreparedListener { mp ->
                    isPrepared = true
                    adjustVideoSize(mp)
                    mp.start()
                    startProgressUpdates()
                    onPrepared()
                    Log.d(TAG, "MediaPlayer prepared, duration: ${mp.duration}ms")
                }

                setOnCompletionListener {
                    stopProgressUpdates()
                    onCompletion()
                }

                setOnErrorListener { _, what, extra ->
                    Log.e(TAG, "MediaPlayer error: what=$what, extra=$extra")
                    isPrepared = false
                    stopProgressUpdates()
                    onError(what, extra)
                    true
                }

                setOnVideoSizeChangedListener { mp, width, height ->
                    if (width > 0 && height > 0) {
                        adjustVideoSize(mp)
                    }
                }

                setOnBufferingUpdateListener { _, percent ->
                    Log.d(TAG, "Buffering: $percent%")
                }

                setOnInfoListener { _, what, _ ->
                    when (what) {
                        MediaPlayer.MEDIA_INFO_BUFFERING_START ->
                            Log.d(TAG, "Buffering started")
                        MediaPlayer.MEDIA_INFO_BUFFERING_END ->
                            Log.d(TAG, "Buffering ended")
                        MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START ->
                            Log.d(TAG, "Video rendering started")
                    }
                    false
                }

                prepareAsync()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to setup MediaPlayer", e)
            onError(-1, -1)
        }
    }

    private fun adjustVideoSize(mp: MediaPlayer) {
        val videoWidth = mp.videoWidth
        val videoHeight = mp.videoHeight
        if (videoWidth == 0 || videoHeight == 0) return

        val viewWidth = surfaceView.width
        val viewHeight = surfaceView.height
        if (viewWidth == 0 || viewHeight == 0) return

        val videoRatio = videoWidth.toFloat() / videoHeight.toFloat()
        val viewRatio = viewWidth.toFloat() / viewHeight.toFloat()

        val layoutParams = surfaceView.layoutParams
        if (videoRatio > viewRatio) {
            // Video is wider than view — fit width
            layoutParams.width = viewWidth
            layoutParams.height = (viewWidth / videoRatio).toInt()
        } else {
            // Video is taller than view — fit height
            layoutParams.height = viewHeight
            layoutParams.width = (viewHeight * videoRatio).toInt()
        }
        surfaceView.layoutParams = layoutParams
    }

    // ── Playback Controls ───────────────────────────────────────────────────

    fun pause() {
        if (isPrepared) {
            mediaPlayer?.pause()
            stopProgressUpdates()
        }
    }

    fun resume() {
        if (isPrepared && mediaPlayer?.isPlaying == false) {
            mediaPlayer?.start()
            startProgressUpdates()
        }
    }

    fun togglePlayPause() {
        if (isPrepared) {
            if (mediaPlayer?.isPlaying == true) pause() else resume()
        }
    }

    fun seekForward() {
        seekBy(SEEK_INCREMENT_MS)
    }

    fun seekBackward() {
        seekBy(-SEEK_INCREMENT_MS)
    }

    fun seekTo(positionMs: Int) {
        if (isPrepared) {
            mediaPlayer?.seekTo(positionMs.coerceIn(0, mediaPlayer?.duration ?: 0))
        }
    }

    private fun seekBy(deltaMs: Int) {
        if (isPrepared) {
            val current = mediaPlayer?.currentPosition ?: 0
            val duration = mediaPlayer?.duration ?: 0
            val newPos = (current + deltaMs).coerceIn(0, duration)
            mediaPlayer?.seekTo(newPos)
        }
    }

    val isPlaying: Boolean get() = isPrepared && mediaPlayer?.isPlaying == true
    val currentPosition: Int get() = if (isPrepared) mediaPlayer?.currentPosition ?: 0 else 0
    val duration: Int get() = if (isPrepared) mediaPlayer?.duration ?: 0 else 0

    // ── D-Pad Controls ──────────────────────────────────────────────────────

    private fun setupDpadControls() {
        surfaceView.isFocusable = true
        surfaceView.isFocusableInTouchMode = true
        surfaceView.requestFocus()

        surfaceView.setOnKeyListener { _, keyCode, event ->
            if (event.action != KeyEvent.ACTION_DOWN) return@setOnKeyListener false

            when (keyCode) {
                KeyEvent.KEYCODE_DPAD_CENTER,
                KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE,
                KeyEvent.KEYCODE_ENTER -> {
                    togglePlayPause()
                    true
                }
                KeyEvent.KEYCODE_MEDIA_PLAY -> {
                    resume()
                    true
                }
                KeyEvent.KEYCODE_MEDIA_PAUSE -> {
                    pause()
                    true
                }
                KeyEvent.KEYCODE_DPAD_RIGHT,
                KeyEvent.KEYCODE_MEDIA_FAST_FORWARD -> {
                    seekForward()
                    true
                }
                KeyEvent.KEYCODE_DPAD_LEFT,
                KeyEvent.KEYCODE_MEDIA_REWIND -> {
                    seekBackward()
                    true
                }
                KeyEvent.KEYCODE_MEDIA_STOP -> {
                    release()
                    true
                }
                else -> false
            }
        }
    }

    // ── Progress Updates ────────────────────────────────────────────────────

    private fun startProgressUpdates() {
        handler.removeCallbacks(progressRunnable)
        handler.post(progressRunnable)
    }

    private fun stopProgressUpdates() {
        handler.removeCallbacks(progressRunnable)
    }

    // ── Lifecycle ───────────────────────────────────────────────────────────

    fun release() {
        stopProgressUpdates()
        isPrepared = false
        mediaPlayer?.let { mp ->
            try {
                if (mp.isPlaying) mp.stop()
                mp.setDisplay(null)
                mp.reset()
                mp.release()
            } catch (e: Exception) {
                Log.e(TAG, "Error releasing MediaPlayer", e)
            }
        }
        mediaPlayer = null
    }
}
