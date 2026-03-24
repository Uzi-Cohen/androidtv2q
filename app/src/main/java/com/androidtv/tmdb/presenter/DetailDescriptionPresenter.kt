package com.androidtv.tmdb.presenter

import androidx.leanback.widget.AbstractDetailsDescriptionPresenter
import com.androidtv.tmdb.model.MovieDetail
import com.androidtv.tmdb.model.TvShowDetail

class DetailDescriptionPresenter : AbstractDetailsDescriptionPresenter() {

    override fun onBindDescription(viewHolder: ViewHolder, item: Any) {
        when (item) {
            is MovieDetail -> {
                viewHolder.title.text = item.title
                viewHolder.subtitle.text = buildString {
                    item.releaseDate?.take(4)?.let { append(it) }
                    item.runtime?.let {
                        if (isNotEmpty()) append(" · ")
                        append("${it}min")
                    }
                    if (item.genres.isNotEmpty()) {
                        if (isNotEmpty()) append(" · ")
                        append(item.genres.joinToString(", ") { it.name })
                    }
                    if (isNotEmpty()) append(" · ")
                    append("★ ${String.format("%.1f", item.voteAverage)}")
                }
                viewHolder.body.text = buildString {
                    item.tagline?.takeIf { it.isNotBlank() }?.let {
                        append("\"$it\"\n\n")
                    }
                    append(item.overview)
                }
            }
            is TvShowDetail -> {
                viewHolder.title.text = item.name
                viewHolder.subtitle.text = buildString {
                    item.firstAirDate?.take(4)?.let { append(it) }
                    append(" · ${item.numberOfSeasons} Season${if (item.numberOfSeasons != 1) "s" else ""}")
                    if (item.genres.isNotEmpty()) {
                        append(" · ")
                        append(item.genres.joinToString(", ") { it.name })
                    }
                    append(" · ★ ${String.format("%.1f", item.voteAverage)}")
                }
                viewHolder.body.text = buildString {
                    item.tagline?.takeIf { it.isNotBlank() }?.let {
                        append("\"$it\"\n\n")
                    }
                    append(item.overview)
                }
            }
        }
    }
}
