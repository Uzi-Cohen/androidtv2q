package com.androidtv.tmdb.presenter

import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.leanback.widget.ImageCardView
import androidx.leanback.widget.Presenter
import com.androidtv.tmdb.R
import com.androidtv.tmdb.model.CastMember
import com.bumptech.glide.Glide

class CastCardPresenter : Presenter() {

    companion object {
        private const val CARD_WIDTH = 132
        private const val CARD_HEIGHT = 176
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
        val cast = item as CastMember
        val cardView = viewHolder.view as ImageCardView

        cardView.titleText = cast.name
        cardView.contentText = cast.character

        cast.profileUrl()?.let { url ->
            Glide.with(cardView.context)
                .load(url)
                .centerCrop()
                .placeholder(R.drawable.placeholder_person)
                .error(R.drawable.placeholder_person)
                .into(cardView.mainImageView)
        }
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        val cardView = viewHolder.view as ImageCardView
        cardView.mainImage = null
    }
}
