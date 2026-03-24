package com.androidtv.tmdb.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

// ── Movie ───────────────────────────────────────────────────────────────────

data class MovieResponse(
    val page: Int,
    val results: List<Movie>,
    @SerializedName("total_pages") val totalPages: Int,
    @SerializedName("total_results") val totalResults: Int
)

data class Movie(
    val id: Long,
    val title: String,
    val overview: String,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("release_date") val releaseDate: String?,
    @SerializedName("vote_average") val voteAverage: Double,
    @SerializedName("vote_count") val voteCount: Int,
    @SerializedName("genre_ids") val genreIds: List<Int>?,
    val popularity: Double,
    val adult: Boolean
) : Serializable {

    fun posterUrl(size: String = "w342"): String? =
        posterPath?.let { "https://image.tmdb.org/t/p/$size$it" }

    fun backdropUrl(size: String = "w780"): String? =
        backdropPath?.let { "https://image.tmdb.org/t/p/$size$it" }
}

// ── TV Show ─────────────────────────────────────────────────────────────────

data class TvShowResponse(
    val page: Int,
    val results: List<TvShow>,
    @SerializedName("total_pages") val totalPages: Int,
    @SerializedName("total_results") val totalResults: Int
)

data class TvShow(
    val id: Long,
    val name: String,
    val overview: String,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("first_air_date") val firstAirDate: String?,
    @SerializedName("vote_average") val voteAverage: Double,
    @SerializedName("vote_count") val voteCount: Int,
    @SerializedName("genre_ids") val genreIds: List<Int>?,
    val popularity: Double,
    @SerializedName("origin_country") val originCountry: List<String>?
) : Serializable {

    fun posterUrl(size: String = "w342"): String? =
        posterPath?.let { "https://image.tmdb.org/t/p/$size$it" }

    fun backdropUrl(size: String = "w780"): String? =
        backdropPath?.let { "https://image.tmdb.org/t/p/$size$it" }
}

// ── Videos (Trailers) ───────────────────────────────────────────────────────

data class VideoResponse(
    val id: Long,
    val results: List<Video>
)

data class Video(
    val id: String,
    @SerializedName("iso_639_1") val language: String?,
    val key: String,
    val name: String,
    val site: String,
    val size: Int,
    val type: String,
    val official: Boolean
) : Serializable

// ── Movie Detail ────────────────────────────────────────────────────────────

data class MovieDetail(
    val id: Long,
    val title: String,
    val overview: String,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("release_date") val releaseDate: String?,
    @SerializedName("vote_average") val voteAverage: Double,
    @SerializedName("vote_count") val voteCount: Int,
    val runtime: Int?,
    val tagline: String?,
    val genres: List<Genre>,
    val status: String,
    val revenue: Long,
    val budget: Long
) : Serializable {

    fun posterUrl(size: String = "w500"): String? =
        posterPath?.let { "https://image.tmdb.org/t/p/$size$it" }

    fun backdropUrl(size: String = "w1280"): String? =
        backdropPath?.let { "https://image.tmdb.org/t/p/$size$it" }
}

// ── TV Detail ───────────────────────────────────────────────────────────────

data class TvShowDetail(
    val id: Long,
    val name: String,
    val overview: String,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("first_air_date") val firstAirDate: String?,
    @SerializedName("vote_average") val voteAverage: Double,
    @SerializedName("vote_count") val voteCount: Int,
    @SerializedName("number_of_seasons") val numberOfSeasons: Int,
    @SerializedName("number_of_episodes") val numberOfEpisodes: Int,
    val tagline: String?,
    val genres: List<Genre>,
    val status: String,
    @SerializedName("episode_run_time") val episodeRunTime: List<Int>?
) : Serializable {

    fun posterUrl(size: String = "w500"): String? =
        posterPath?.let { "https://image.tmdb.org/t/p/$size$it" }

    fun backdropUrl(size: String = "w1280"): String? =
        backdropPath?.let { "https://image.tmdb.org/t/p/$size$it" }
}

// ── Credits ─────────────────────────────────────────────────────────────────

data class CreditsResponse(
    val id: Long,
    val cast: List<CastMember>,
    val crew: List<CrewMember>
)

data class CastMember(
    val id: Long,
    val name: String,
    val character: String,
    @SerializedName("profile_path") val profilePath: String?,
    val order: Int
) : Serializable {

    fun profileUrl(size: String = "w185"): String? =
        profilePath?.let { "https://image.tmdb.org/t/p/$size$it" }
}

data class CrewMember(
    val id: Long,
    val name: String,
    val job: String,
    val department: String,
    @SerializedName("profile_path") val profilePath: String?
) : Serializable

// ── Shared ──────────────────────────────────────────────────────────────────

data class Genre(
    val id: Int,
    val name: String
) : Serializable

// ── Multi-search ────────────────────────────────────────────────────────────

data class MultiSearchResponse(
    val page: Int,
    val results: List<SearchResult>,
    @SerializedName("total_pages") val totalPages: Int,
    @SerializedName("total_results") val totalResults: Int
)

data class SearchResult(
    val id: Long,
    @SerializedName("media_type") val mediaType: String,
    val title: String?,          // movies
    val name: String?,           // tv shows / people
    val overview: String?,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("vote_average") val voteAverage: Double?,
    @SerializedName("release_date") val releaseDate: String?,
    @SerializedName("first_air_date") val firstAirDate: String?,
    @SerializedName("profile_path") val profilePath: String?
) : Serializable {

    val displayTitle: String get() = title ?: name ?: "Unknown"

    fun posterUrl(size: String = "w342"): String? {
        val path = posterPath ?: profilePath
        return path?.let { "https://image.tmdb.org/t/p/$size$it" }
    }

    fun backdropUrl(size: String = "w780"): String? =
        backdropPath?.let { "https://image.tmdb.org/t/p/$size$it" }
}
