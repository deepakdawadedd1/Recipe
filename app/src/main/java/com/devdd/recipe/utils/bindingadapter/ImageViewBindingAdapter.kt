package com.devdd.recipe.utils.bindingadapter

import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.BindingAdapter
import coil.load
import coil.transform.CircleCropTransformation
import coil.transform.RoundedCornersTransformation
import com.devdd.recipe.R
import com.devdd.recipe.utils.extensions.animateAVD
import com.devdd.recipe.utils.extensions.px

@BindingAdapter(
    "loadImage",
    "circleCrop",
    "roundCorner",
    "cornerRadius",
    requireAll = false
)
fun AppCompatImageView.loadImage(
    imageUrl: String?,
    circleCrop: Boolean? = false,
    roundCorner: Boolean? = false,
    cornerRadius: Int? = 0
) {
    load(imageUrl) {
        placeholder(R.drawable.ic_launcher_foreground)
        if (circleCrop == true)
            transformations(CircleCropTransformation())
        else if (roundCorner == true)
            transformations(RoundedCornersTransformation(radius = (cornerRadius ?: 0).px(context)))
        error(R.drawable.ic_launcher_foreground)
    }
}

@BindingAdapter("avdLoop")
fun AppCompatImageView.animateAVD(loop: Boolean) {
    animateAVD(loop)
}

@BindingAdapter("srcVectorRes")
fun AppCompatImageView.setSrcVectorRes(@DrawableRes drawable: Int) {
    setImageResource(drawable)
}

@BindingAdapter("srcVectorDrawable")
fun AppCompatImageView.setSrcVectorDrawable(drawable: Drawable) {
    setImageDrawable(drawable)
}


@BindingAdapter("imageTint")
fun AppCompatImageView.tintColorName(color: Int) {
    imageTintList = ColorStateList.valueOf(color)
}
