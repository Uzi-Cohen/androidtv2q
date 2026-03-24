package com.androidtv.tmdb.api

import com.androidtv.tmdb.model.*

class TmdbRepository(private val api: TmdbApi = TmdbClient.api) {

    // ── Movies ──────────────────────────────────────────────────────────────

    suspend fun getPopularMovies(page: Int = 1): Result<List<Movie>> = runCatching {
        api.getPopularMovies(page).results
    }

    suspend fun getTopRatedMovies(page: Int = 1): Result<List<Movie>> = runCatching {
        api.getTopRatedMovies(page).results
    }

    suspend fun getNowPlayingMovies(page: Int = 1): Result<List<Movie>> = runCatching {
        api.getNowPlayingMovies(page).results
    }

    suspend fun getUpcomingMovies(page: Int = 1): Result<List<Movie>> = runCatching {
        api.getUpcomingMovies(page).results
    }

    suspend fun getTrendingMovies(): Result<List<Movie>> = runCatching {
        api.getTrendingMovies().results
    }

    suspend fun getMovieDetail(movieId: Long): Result<MovieDetail> = runCatching {
        api.getMovieDetail(movieId)
    }

    suspend fun getMovieCredits(movieId: Long): Result<CreditsResponse> = runCatching {
        api.getMovieCredits(movieId)
    }

    suspend fun getMovieVideos(movieId: Long): Result<List<Video>> = runCatching {
        api.getMovieVideos(movieId).results
    }

    suspend fun getSimilarMovies(movieId: Long): Result<List<Movie>> = runCatching {
        api.getSimilarMovies(movieId).results
    }

    suspend fun getMovieRecommendations(movieId: Long): Result<List<Movie>> = runCatching {
        api.getMovieRecommendations(movieId).results
    }

    // ── TV Shows ────────────────────────────────────────────────────────────

    suspend fun getPopularTvShows(page: Int = 1): Result<List<TvShow>> = runCatching {
        api.getPopularTvShows(page).results
    }

    suspend fun getTopRatedTvShows(page: Int = 1): Result<List<TvShow>> = runCatching {
        api.getTopRatedTvShows(page).results
    }

    suspend fun getAiringTodayTvShows(page: Int = 1): Result<List<TvShow>> = runCatching {
        api.getAiringTodayTvShows(page).results
    }

    suspend fun getOnTheAirTvShows(page: Int = 1): Result<List<TvShow>> = runCatching {
        api.getOnTheAirTvShows(page).results
    }

    suspend fun getTrendingTvShows(): Result<List<TvShow>> = runCatching {
        api.getTrendingTvShows().results
    }

    suspend fun getTvShowDetail(tvId: Long): Result<TvShowDetail> = runCatching {
        api.getTvShowDetail(tvId)
    }

    suspend fun getTvShowCredits(tvId: Long): Result<CreditsResponse> = runCatching {
        api.getTvShowCredits(tvId)
    }

    suspend fun getTvShowVideos(tvId: Long): Result<List<Video>> = runCatching {
        api.getTvShowVideos(tvId).results
    }

    suspend fun getSimilarTvShows(tvId: Long): Result<List<TvShow>> = runCatching {
        api.getSimilarTvShows(tvId).results
    }

    // ── Search ──────────────────────────────────────────────────────────────

    suspend fun searchMulti(query: String, page: Int = 1): Result<List<SearchResult>> = runCatching {
        api.searchMulti(query, page).results
    }
}
