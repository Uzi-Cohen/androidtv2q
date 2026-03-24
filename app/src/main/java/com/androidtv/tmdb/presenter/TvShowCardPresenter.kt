package com.androidtv.tmdb.presenter

import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.leanback.widget.ImageCardView
import androidx.leanback.widget.Presenter
import com.androidtv.tmdb.R
import com.androidtv.tmdb.model.TvShow
import com.bumptech.glide.Glide

class TvShowCardPresenter : Presenter() {

    companion object {
        private const val CARD_WIDTH = 176
        private const val CARD_HEIGHT = 264
    }

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val cardView = ImageCardView(parent.context).apply {
            isFocusable = true
            isFocusableInTouchMode = true
            setMainImageDimensions(CARD_WIDTH, CARD_HEIGHT)
            setBackgroundColor(ContextCompat.getColor(context, R.color.card_background))
        }
        return ViewHolder(cardView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val tvShow = item as TvShow
        val cardView = viewHolder.view as ImageCardView

        cardView.titleText = tvShow.name
        cardView.contentText = "★ ${String.format("%.1f", tvShow.voteAverage)}"

        tvShow.posterUrl()?.let { url ->
            Glide.with(cardView.context)
                .load(url)
                .centerCrop()
                .placeholder(R.drawable.placeholder_poster)
                .error(R.drawable.placeholder_poster)
                .into(cardView.mainImageView)
        }
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        val cardView = viewHolder.view as ImageCardView
        cardView.mainImage = null
    }
}
