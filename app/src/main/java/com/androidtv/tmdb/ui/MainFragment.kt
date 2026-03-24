package com.androidtv.tmdb.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.*
import androidx.lifecycle.lifecycleScope
import com.androidtv.tmdb.R
import com.androidtv.tmdb.api.TmdbRepository
import com.androidtv.tmdb.model.Movie
import com.androidtv.tmdb.model.TvShow
import com.androidtv.tmdb.presenter.MovieCardPresenter
import com.androidtv.tmdb.presenter.TvShowCardPresenter
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MainFragment : BrowseSupportFragment() {

    private val repository = TmdbRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUI()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadContent()
    }

    private fun setupUI() {
        title = getString(R.string.app_name)
        headersState = HEADERS_ENABLED
        isHeadersTransitionOnBackEnabled = true
        brandColor = ContextCompat.getColor(requireContext(), R.color.brand_color)
        searchAffordanceColor = Color.WHITE

        setOnSearchClickedListener {
            startActivity(Intent(requireContext(), SearchActivity::class.java))
        }

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

        setOnItemViewSelectedListener { _, item, _, row ->
            if (item is Movie) {
                // Could update background here
            }
        }
    }

    private fun loadContent() {
        val rowsAdapter = ArrayObjectAdapter(ListRowPresenter())

        viewLifecycleOwner.lifecycleScope.launch {
            val trendingMovies = async { repository.getTrendingMovies() }
            val popularMovies = async { repository.getPopularMovies() }
            val topRatedMovies = async { repository.getTopRatedMovies() }
            val nowPlaying = async { repository.getNowPlayingMovies() }
            val upcoming = async { repository.getUpcomingMovies() }
            val trendingTv = async { repository.getTrendingTvShows() }
            val popularTv = async { repository.getPopularTvShows() }
            val topRatedTv = async { repository.getTopRatedTvShows() }
            val airingToday = async { repository.getAiringTodayTvShows() }

            var rowIndex = 0

            // Trending Movies
            trendingMovies.await().onSuccess { movies ->
                rowsAdapter.add(createMovieRow(rowIndex++, "Trending Movies", movies))
            }

            // Popular Movies
            popularMovies.await().onSuccess { movies ->
                rowsAdapter.add(createMovieRow(rowIndex++, "Popular Movies", movies))
            }

            // Top Rated Movies
            topRatedMovies.await().onSuccess { movies ->
                rowsAdapter.add(createMovieRow(rowIndex++, "Top Rated Movies", movies))
            }

            // Now Playing
            nowPlaying.await().onSuccess { movies ->
                rowsAdapter.add(createMovieRow(rowIndex++, "Now Playing", movies))
            }

            // Upcoming Movies
            upcoming.await().onSuccess { movies ->
                rowsAdapter.add(createMovieRow(rowIndex++, "Upcoming Movies", movies))
            }

            // Trending TV
            trendingTv.await().onSuccess { shows ->
                rowsAdapter.add(createTvShowRow(rowIndex++, "Trending TV Shows", shows))
            }

            // Popular TV
            popularTv.await().onSuccess { shows ->
                rowsAdapter.add(createTvShowRow(rowIndex++, "Popular TV Shows", shows))
            }

            // Top Rated TV
            topRatedTv.await().onSuccess { shows ->
                rowsAdapter.add(createTvShowRow(rowIndex++, "Top Rated TV Shows", shows))
            }

            // Airing Today
            airingToday.await().onSuccess { shows ->
                rowsAdapter.add(createTvShowRow(rowIndex++, "Airing Today", shows))
            }

            adapter = rowsAdapter

            if (rowsAdapter.size() == 0) {
                Toast.makeText(context, "Failed to load content", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun createMovieRow(id: Int, title: String, movies: List<Movie>): ListRow {
        val cardPresenter = MovieCardPresenter()
        val listRowAdapter = ArrayObjectAdapter(cardPresenter)
        movies.forEach { listRowAdapter.add(it) }
        return ListRow(HeaderItem(id.toLong(), title), listRowAdapter)
    }

    private fun createTvShowRow(id: Int, title: String, shows: List<TvShow>): ListRow {
        val cardPresenter = TvShowCardPresenter()
        val listRowAdapter = ArrayObjectAdapter(cardPresenter)
        shows.forEach { listRowAdapter.add(it) }
        return ListRow(HeaderItem(id.toLong(), title), listRowAdapter)
    }
}
