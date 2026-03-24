package com.androidtv.tmdb.api

import com.androidtv.tmdb.model.*
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApi {

    // ── Movies ──────────────────────────────────────────────────────────────

    @GET("movie/popular")
    suspend fun getPopularMovies(@Query("page") page: Int = 1): MovieResponse

    @GET("movie/top_rated")
    suspend fun getTopRatedMovies(@Query("page") page: Int = 1): MovieResponse

    @GET("movie/now_playing")
    suspend fun getNowPlayingMovies(@Query("page") page: Int = 1): MovieResponse

    @GET("movie/upcoming")
    suspend fun getUpcomingMovies(@Query("page") page: Int = 1): MovieResponse

    @GET("movie/{movie_id}")
    suspend fun getMovieDetail(@Path("movie_id") movieId: Long): MovieDetail

    @GET("movie/{movie_id}/credits")
    suspend fun getMovieCredits(@Path("movie_id") movieId: Long): CreditsResponse

    @GET("movie/{movie_id}/videos")
    suspend fun getMovieVideos(@Path("movie_id") movieId: Long): VideoResponse

    @GET("movie/{movie_id}/similar")
    suspend fun getSimilarMovies(@Path("movie_id") movieId: Long): MovieResponse

    @GET("movie/{movie_id}/recommendations")
    suspend fun getMovieRecommendations(@Path("movie_id") movieId: Long): MovieResponse

    // ── TV Shows ────────────────────────────────────────────────────────────

    @GET("tv/popular")
    suspend fun getPopularTvShows(@Query("page") page: Int = 1): TvShowResponse

    @GET("tv/top_rated")
    suspend fun getTopRatedTvShows(@Query("page") page: Int = 1): TvShowResponse

    @GET("tv/airing_today")
    suspend fun getAiringTodayTvShows(@Query("page") page: Int = 1): TvShowResponse

    @GET("tv/on_the_air")
    suspend fun getOnTheAirTvShows(@Query("page") page: Int = 1): TvShowResponse

    @GET("tv/{tv_id}")
    suspend fun getTvShowDetail(@Path("tv_id") tvId: Long): TvShowDetail

    @GET("tv/{tv_id}/credits")
    suspend fun getTvShowCredits(@Path("tv_id") tvId: Long): CreditsResponse

    @GET("tv/{tv_id}/videos")
    suspend fun getTvShowVideos(@Path("tv_id") tvId: Long): VideoResponse

    @GET("tv/{tv_id}/similar")
    suspend fun getSimilarTvShows(@Path("tv_id") tvId: Long): TvShowResponse

    @GET("tv/{tv_id}/recommendations")
    suspend fun getTvShowRecommendations(@Path("tv_id") tvId: Long): TvShowResponse

    // ── Search ──────────────────────────────────────────────────────────────

    @GET("search/multi")
    suspend fun searchMulti(
        @Query("query") query: String,
        @Query("page") page: Int = 1
    ): MultiSearchResponse

    @GET("search/movie")
    suspend fun searchMovies(
        @Query("query") query: String,
        @Query("page") page: Int = 1
    ): MovieResponse

    @GET("search/tv")
    suspend fun searchTvShows(
        @Query("query") query: String,
        @Query("page") page: Int = 1
    ): TvShowResponse

    // ── Trending ────────────────────────────────────────────────────────────

    @GET("trending/movie/{time_window}")
    suspend fun getTrendingMovies(
        @Path("time_window") timeWindow: String = "week"
    ): MovieResponse

    @GET("trending/tv/{time_window}")
    suspend fun getTrendingTvShows(
        @Path("time_window") timeWindow: String = "week"
    ): TvShowResponse
}
