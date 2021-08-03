package com.niran.psychoquiz.utils

import android.graphics.Color
import android.os.Build
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.cardview.widget.CardView
import com.google.android.material.color.MaterialColors


object UiUtil {

    fun setViewsIsClickable(isClickable: Boolean, vararg views: View) {
        for (view in views) view.isClickable = isClickable
    }


    fun setViewsBackgroundColor(colorId: Int, vararg views: View) {
        for (view in views) view.customSetBackgroundColor(colorId)
    }

    private fun View.customSetBackgroundColor(colorId: Int) {
        when (this) {
            is CardView ->
                setCardBackgroundColor(MaterialColors.getColor(context, colorId, DEFAULT_COLOR))

            is ImageView -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                background.setTint(MaterialColors.getColor(context, colorId, DEFAULT_COLOR))

            is Button -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                background.setTint(MaterialColors.getColor(context, colorId, DEFAULT_COLOR))
            else setBackgroundColor(MaterialColors.getColor(context, colorId, DEFAULT_COLOR))

            else -> setBackgroundColor(MaterialColors.getColor(context, colorId, DEFAULT_COLOR))
        }

    }

    private const val DEFAULT_COLOR = Color.CYAN
}

