package com.dev.petmarket_android.common.ui

import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.dev.petmarket_android.BuildConfig
import com.dev.petmarket_android.R

object ImageLoader {

    fun load(
        imageView: ImageView,
        imageSource: String?,
        circleCrop: Boolean = false,
        placeholderColor: Int = R.color.gray_100
    ) {
        val resolvedSource = resolveImageSource(imageSource)
        if (resolvedSource == null) {
            imageView.setImageDrawable(null)
            imageView.setBackgroundColor(ContextCompat.getColor(imageView.context, placeholderColor))
            return
        }

        val requestOptions = if (circleCrop) {
            RequestOptions.circleCropTransform()
        } else {
            RequestOptions.centerCropTransform()
        }

        Glide.with(imageView)
            .load(resolvedSource)
            .apply(requestOptions)
            .placeholder(placeholderColor)
            .error(placeholderColor)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(imageView)
    }

    private fun resolveImageSource(imageSource: String?): String? {
        val source = imageSource?.trim().orEmpty()
        if (source.isBlank()) {
            return null
        }

        if (source.startsWith("http://", true) ||
            source.startsWith("https://", true) ||
            source.startsWith("data:image/", true)
        ) {
            return source
        }

        val baseUrl = BuildConfig.API_BASE_URL.trimEnd('/')
        return if (source.startsWith("/")) {
            baseUrl + source
        } else {
            "$baseUrl/$source"
        }
    }
}
