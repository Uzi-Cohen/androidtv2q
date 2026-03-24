package com.androidtv.tmdb.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.leanback.app.SearchSupportFragment
import androidx.leanback.widget.*
import androidx.lifecycle.lifecycleScope
import com.androidtv.tmdb.api.TmdbRepository
import com.androidtv.tmdb.model.SearchResult
import com.androidtv.tmdb.presenter.SearchCardPresenter
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SearchFragment : SearchSupportFragment(), SearchSupportFragment.SearchResultProvider {

    private val repository = TmdbRepository()
    private val rowsAdapter = ArrayObjectAdapter(ListRowPresenter())
    private val handler = Handler(Looper.getMainLooper())
    private var searchJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSearchResultProvider(this)

        setOnItemViewClickedListener { _, item, _, _ ->
            if (item is SearchResult) {
                val intent = Intent(requireContext(), DetailActivity::class.java).apply {
                    putExtra(DetailActivity.EXTRA_MEDIA_ID, item.id)
                    putExtra(
                        DetailActivity.EXTRA_MEDIA_TYPE,
                        if (item.mediaType == "movie") "movie" else "tv"
                    )
                }
                startActivity(intent)
            }
        }
    }

    override fun getResultsAdapter(): ObjectAdapter = rowsAdapter

    override fun onQueryTextChange(newQuery: String): Boolean {
        handler.removeCallbacksAndMessages(null)
        handler.postDelayed({ performSearch(newQuery) }, 400)
        return true
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        handler.removeCallbacksAndMessages(null)
        performSearch(query)
        return true
    }

    private fun performSearch(query: String) {
        if (query.length < 2) {
            rowsAdapter.clear()
            return
        }

        searchJob?.cancel()
        searchJob = viewLifecycleOwner.lifecycleScope.launch {
            repository.searchMulti(query).onSuccess { results ->
                rowsAdapter.clear()

                val movies = results.filter { it.mediaType == "movie" }
                val tvShows = results.filter { it.mediaType == "tv" }

                val searchPresenter = SearchCardPresenter()

                if (movies.isNotEmpty()) {
                    val movieAdapter = ArrayObjectAdapter(searchPresenter)
                    movies.forEach { movieAdapter.add(it) }
                    rowsAdapter.add(ListRow(HeaderItem(0, "Movies"), movieAdapter))
                }

                if (tvShows.isNotEmpty()) {
                    val tvAdapter = ArrayObjectAdapter(searchPresenter)
                    tvShows.forEach { tvAdapter.add(it) }
                    rowsAdapter.add(ListRow(HeaderItem(1, "TV Shows"), tvAdapter))
                }
            }
        }
    }
}
