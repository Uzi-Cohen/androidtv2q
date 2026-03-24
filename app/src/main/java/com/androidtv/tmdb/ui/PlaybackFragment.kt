package com.androidtv.tmdb.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.androidtv.tmdb.R
import com.androidtv.tmdb.model.Video

class PlaybackFragment : Fragment() {

    companion object {
        private const val ARG_VIDEO = "arg_video"
        private const val ARG_TITLE = "arg_title"

        fun newInstance(video: Video?, title: String): PlaybackFragment {
            return PlaybackFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_VIDEO, video)
                    putString(ARG_TITLE, title)
                }
            }
        }
    }

    private var player: ExoPlayer? = null
    private var playerView: PlayerView? = null
    private var video: Video? = null
    private var title: String = "Video"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        @Suppress("DEPRECATION")
        video = arguments?.getSerializable(ARG_VIDEO) as? Video
        title = arguments?.getString(ARG_TITLE, "Video") ?: "Video"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_playback, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playerView = view.findViewById(R.id.player_view)

        val video = this.video
        if (video == null) {
            Toast.makeText(context, "No video to play", Toast.LENGTH_SHORT).show()
            activity?.finish()
            return
        }

        if (video.site.equals("YouTube", ignoreCase = true)) {
            playYouTube(video.key)
        } else {
            setupExoPlayer(video.key)
        }
    }

    private fun playYouTube(videoKey: String) {
        // Try YouTube TV app first, then any browser, then fall back to demo video
        val launched = tryStartActivity(
            Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:$videoKey"))
        ) || tryStartActivity(
            Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=$videoKey"))
        )

        if (launched) {
            activity?.finish()
        } else {
            // No YouTube app or browser available — play demo video with controls
            setupExoPlayer("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4")
        }
    }

    private fun tryStartActivity(intent: Intent): Boolean {
        return try {
            startActivity(intent)
            true
        } catch (e: ActivityNotFoundException) {
            false
        }
    }

    private fun setupExoPlayer(url: String) {
        player = ExoPlayer.Builder(requireContext()).build().also { exoPlayer ->
            playerView?.player = exoPlayer
            exoPlayer.setMediaItem(MediaItem.fromUri(url))
            exoPlayer.prepare()
            exoPlayer.playWhenReady = true
        }
    }

    override fun onPause() {
        super.onPause()
        player?.pause()
    }

    override fun onResume() {
        super.onResume()
        player?.play()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        player?.release()
        player = null
        playerView?.player = null
        playerView = null
    }
}
