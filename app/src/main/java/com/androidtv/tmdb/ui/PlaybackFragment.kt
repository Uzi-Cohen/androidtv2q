package com.androidtv.tmdb.ui

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.leanback.app.PlaybackSupportFragment
import androidx.leanback.widget.Action
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.ClassPresenterSelector
import androidx.leanback.widget.ControlButtonPresenterSelector
import androidx.leanback.widget.ListRow
import androidx.leanback.widget.ListRowPresenter
import androidx.leanback.widget.PlaybackControlsRow
import androidx.leanback.widget.PlaybackControlsRowPresenter
import com.androidtv.tmdb.R
import com.androidtv.tmdb.model.Video
import com.androidtv.tmdb.player.TvMediaPlayer

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

    private var tvMediaPlayer: TvMediaPlayer? = null
    private var surfaceView: SurfaceView? = null
    private var progressBar: ProgressBar? = null
    private var video: Video? = null
    private var title: String = "Video"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        surfaceView = view.findViewById(R.id.surface_view)
        progressBar = view.findViewById(R.id.loading_progress)

        val video = this.video
        if (video == null) {
            Toast.makeText(context, "No video to play", Toast.LENGTH_SHORT).show()
            activity?.finish()
            return
        }

        if (video.site.equals("YouTube", ignoreCase = true)) {
            // YouTube videos can't be played directly via MediaPlayer.
            // Show a message with the video info instead, or use a WebView-based player.
            showYouTubeMessage(video)
        } else {
            setupMediaPlayer(video)
        }
    }

    private fun showYouTubeMessage(video: Video) {
        progressBar?.visibility = View.GONE
        Toast.makeText(
            context,
            "Trailer: ${video.name}\nYouTube Key: ${video.key}\n\nFor production, integrate a YouTube player or WebView.",
            Toast.LENGTH_LONG
        ).show()

        // In a real app you'd use YouTube Android Player API or a WebView here.
        // For demo purposes with MediaPlayer, we show the info and demonstrate
        // MediaPlayer with a sample MP4 stream.
        setupDemoMediaPlayer()
    }

    private fun setupDemoMediaPlayer() {
        // Demonstrate MediaPlayer with a publicly available sample video
        val sampleUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
        tvMediaPlayer = TvMediaPlayer(
            context = requireContext(),
            surfaceView = surfaceView!!,
            onPrepared = {
                progressBar?.visibility = View.GONE
            },
            onError = { what, extra ->
                progressBar?.visibility = View.GONE
                Toast.makeText(context, "Playback error ($what)", Toast.LENGTH_SHORT).show()
            },
            onCompletion = {
                activity?.finish()
            }
        )
        tvMediaPlayer?.play(sampleUrl, title)
    }

    private fun setupMediaPlayer(video: Video) {
        // For non-YouTube sources that provide direct URLs
        val videoUrl = video.key // In practice, construct the actual URL

        tvMediaPlayer = TvMediaPlayer(
            context = requireContext(),
            surfaceView = surfaceView!!,
            onPrepared = {
                progressBar?.visibility = View.GONE
            },
            onError = { what, extra ->
                progressBar?.visibility = View.GONE
                Toast.makeText(context, "Playback error ($what)", Toast.LENGTH_SHORT).show()
            },
            onCompletion = {
                activity?.finish()
            }
        )
        tvMediaPlayer?.play(videoUrl, title)
    }

    override fun onPause() {
        super.onPause()
        tvMediaPlayer?.pause()
    }

    override fun onResume() {
        super.onResume()
        tvMediaPlayer?.resume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tvMediaPlayer?.release()
        tvMediaPlayer = null
        surfaceView = null
        progressBar = null
    }
}
