package com.androidtv.tmdb.ui

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.leanback.app.DetailsSupportFragment
import androidx.leanback.widget.*
import androidx.lifecycle.lifecycleScope
import com.androidtv.tmdb.R
import com.androidtv.tmdb.api.TmdbRepository
import com.androidtv.tmdb.model.*
import com.androidtv.tmdb.presenter.DetailDescriptionPresenter
import com.androidtv.tmdb.presenter.MovieCardPresenter
import com.androidtv.tmdb.presenter.TvShowCardPresenter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class DetailFragment : DetailsSupportFragment() {

    companion object {
        private const val ARG_MEDIA_ID = "media_id"
        private const val ARG_MEDIA_TYPE = "media_type"
        private const val ACTION_PLAY = 1L

        fun newInstance(mediaId: Long, mediaType: String): DetailFragment {
            return DetailFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_MEDIA_ID, mediaId)
                    putString(ARG_MEDIA_TYPE, mediaType)
                }
            }
        }
    }

    private val repository = TmdbRepository()
    private lateinit var rowsAdapter: ArrayObjectAdapter
    private var mediaId: Long = -1
    private var mediaType: String = "movie"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mediaId = arguments?.getLong(ARG_MEDIA_ID, -1) ?: -1
        mediaType = arguments?.getString(ARG_MEDIA_TYPE, "movie") ?: "movie"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapter()
        loadDetails()
    }

    private fun setupAdapter() {
        val selector = ClassPresenterSelector().apply {
            addClassPresenter(
                DetailsOverviewRow::class.java,
                FullWidthDetailsOverviewRowPresenter(DetailDescriptionPresenter()).apply {
                    backgroundColor = ContextCompat.getColor(requireContext(), R.color.detail_background)
                    setOnActionClickedListener { action ->
                        when (action.id) {
                            ACTION_PLAY -> playContent()
                        }
                    }
                }
            )
            addClassPresenter(ListRow::class.java, ListRowPresenter())
        }
        rowsAdapter = ArrayObjectAdapter(selector)
        adapter = rowsAdapter

        setOnItemViewClickedListener { _, item, _, _ ->
            when (item) {
                is Movie -> {
                    val intent = Intent(requireContext(), DetailActivity::class.java).apply {
                        putExtra(DetailActivity.EXTRA_MEDIA_ID, item.id)
                        putExtra(DetailActivity.EXTRA_MEDIA_TYPE, "movie")
                    }
                    startActivity(intent)
                }
                is TvShow -> {
                    val intent = Intent(requireContext(), DetailActivity::class.java).apply {
                        putExtra(DetailActivity.EXTRA_MEDIA_ID, item.id)
                        putExtra(DetailActivity.EXTRA_MEDIA_TYPE, "tv")
                    }
                    startActivity(intent)
                }
            }
        }
    }

    private fun loadDetails() {
        viewLifecycleOwner.lifecycleScope.launch {
            if (mediaType == "movie") loadMovieDetails()
            else loadTvShowDetails()
        }
    }

    private suspend fun loadMovieDetails() {
        val detailResult = repository.getMovieDetail(mediaId)
        detailResult.onFailure {
            Toast.makeText(context, "Failed to load details", Toast.LENGTH_SHORT).show()
            return
        }

        val detail = detailResult.getOrReturn() ?: return
        val detailRow = DetailsOverviewRow(detail)

        // Actions
        detailRow.actionsAdapter = ArrayObjectAdapter().apply {
            add(Action(ACTION_PLAY, "Play", null))
        }

        // Load poster image
        loadPosterInto(detail.posterUrl(), detailRow)
        rowsAdapter.add(detailRow)

        // Load related content
        val similarDeferred = viewLifecycleOwner.lifecycleScope.async {
            repository.getSimilarMovies(mediaId)
        }
        val recommendedDeferred = viewLifecycleOwner.lifecycleScope.async {
            repository.getMovieRecommendations(mediaId)
        }
        val creditsDeferred = viewLifecycleOwner.lifecycleScope.async {
            repository.getMovieCredits(mediaId)
        }

        // Cast row
        creditsDeferred.await().onSuccess { credits ->
            if (credits.cast.isNotEmpty()) {
                val castPresenter = com.androidtv.tmdb.presenter.CastCardPresenter()
                val castAdapter = ArrayObjectAdapter(castPresenter)
                credits.cast.take(15).forEach { castAdapter.add(it) }
                rowsAdapter.add(ListRow(HeaderItem(1, "Cast"), castAdapter))
            }
        }

        // Similar movies
        similarDeferred.await().onSuccess { movies ->
            if (movies.isNotEmpty()) {
                val moviePresenter = MovieCardPresenter()
                val movieAdapter = ArrayObjectAdapter(moviePresenter)
                movies.forEach { movieAdapter.add(it) }
                rowsAdapter.add(ListRow(HeaderItem(2, "Similar Movies"), movieAdapter))
            }
        }

        // Recommendations
        recommendedDeferred.await().onSuccess { movies ->
            if (movies.isNotEmpty()) {
                val moviePresenter = MovieCardPresenter()
                val movieAdapter = ArrayObjectAdapter(moviePresenter)
                movies.forEach { movieAdapter.add(it) }
                rowsAdapter.add(ListRow(HeaderItem(3, "Recommended"), movieAdapter))
            }
        }
    }

    private suspend fun loadTvShowDetails() {
        val detailResult = repository.getTvShowDetail(mediaId)
        detailResult.onFailure {
            Toast.makeText(context, "Failed to load details", Toast.LENGTH_SHORT).show()
            return
        }

        val detail = detailResult.getOrReturn() ?: return
        val detailRow = DetailsOverviewRow(detail)

        detailRow.actionsAdapter = ArrayObjectAdapter().apply {
            add(Action(ACTION_PLAY, "Play", null))
        }

        loadPosterInto(detail.posterUrl(), detailRow)
        rowsAdapter.add(detailRow)

        // Load related content
        val similarDeferred = viewLifecycleOwner.lifecycleScope.async {
            repository.getSimilarTvShows(mediaId)
        }
        val creditsDeferred = viewLifecycleOwner.lifecycleScope.async {
            repository.getTvShowCredits(mediaId)
        }

        creditsDeferred.await().onSuccess { credits ->
            if (credits.cast.isNotEmpty()) {
                val castPresenter = com.androidtv.tmdb.presenter.CastCardPresenter()
                val castAdapter = ArrayObjectAdapter(castPresenter)
                credits.cast.take(15).forEach { castAdapter.add(it) }
                rowsAdapter.add(ListRow(HeaderItem(1, "Cast"), castAdapter))
            }
        }

        similarDeferred.await().onSuccess { shows ->
            if (shows.isNotEmpty()) {
                val tvPresenter = TvShowCardPresenter()
                val tvAdapter = ArrayObjectAdapter(tvPresenter)
                shows.forEach { tvAdapter.add(it) }
                rowsAdapter.add(ListRow(HeaderItem(2, "Similar TV Shows"), tvAdapter))
            }
        }
    }

    private fun playContent() {
        val title = if (mediaType == "movie") {
            (rowsAdapter.get(0) as? DetailsOverviewRow)?.item
                ?.let { (it as? MovieDetail)?.title }
        } else {
            (rowsAdapter.get(0) as? DetailsOverviewRow)?.item
                ?.let { (it as? TvShowDetail)?.name }
        } ?: "Video"

        val intent = Intent(requireContext(), PlaybackActivity::class.java).apply {
            putExtra(PlaybackActivity.EXTRA_MEDIA_ID, mediaId)
            putExtra(PlaybackActivity.EXTRA_MEDIA_TYPE, mediaType)
            putExtra(PlaybackActivity.EXTRA_TITLE, title)
        }
        startActivity(intent)
    }

    private fun loadPosterInto(url: String?, row: DetailsOverviewRow) {
        if (url == null) return
        Glide.with(requireContext())
            .asBitmap()
            .load(url)
            .into(object : CustomTarget<Bitmap>(274, 410) {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    row.setImageBitmap(requireContext(), resource)
                }
                override fun onLoadCleared(placeholder: Drawable?) {}
            })
    }

    private fun <T> Result<T>.getOrReturn(): T? = getOrNull()
}
