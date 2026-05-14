package com.example.mahilashaktiunnativ2.ui

import android.content.Context
import androidx.core.content.ContextCompat
import com.example.mahilashaktiunnativ2.R
import com.yalantis.ucrop.UCrop

fun safeCropOptions(
    context: Context
): UCrop.Options {
    val green =
        ContextCompat.getColor(
            context,
            R.color.green_700
        )

    return UCrop.Options().apply {
        setToolbarTitle("Adjust Photo")
        setToolbarColor(green)
        setStatusBarColor(green)
        setActiveControlsWidgetColor(green)
        setToolbarWidgetColor(android.graphics.Color.WHITE)
        setHideBottomControls(false)
        setFreeStyleCropEnabled(false)
    }
}
