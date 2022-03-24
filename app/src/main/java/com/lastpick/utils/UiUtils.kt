package com.lastpick.utils

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.view.WindowInsets
import androidx.annotation.DrawableRes
import androidx.core.view.WindowInsetsCompat


fun cutPicture(activity: Activity, @DrawableRes draw: Int): Bitmap? {
    val screenHeight = activity.windowHeight
    val screenWidth = activity.windowWidth
    val resourceStatus = activity.resources.getIdentifier("status_bar_height", "dimen", "android")
    val statusBarHeight = activity.resources.getDimensionPixelSize(resourceStatus)
    val bitmapTransform = BitmapTransform(screenWidth, screenHeight - statusBarHeight)
    val background = BitmapFactory.decodeResource(activity.resources, draw)
    return bitmapTransform.transform(background)
}


inline val Activity.windowHeight: Int
    get() {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val metrics = windowManager.currentWindowMetrics
            val insets = metrics.windowInsets.getInsets(WindowInsets.Type.systemBars())
            metrics.bounds.height() - insets.bottom - insets.top
        } else {
            val view = window.decorView
            val insets = WindowInsetsCompat.toWindowInsetsCompat(view.rootWindowInsets, view)
                .getInsets(WindowInsetsCompat.Type.systemBars())
            resources.displayMetrics.heightPixels - insets.bottom - insets.top
        }
    }

inline val Activity.windowWidth: Int
    get() {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val metrics = windowManager.currentWindowMetrics
            val insets = metrics.windowInsets.getInsets(WindowInsets.Type.systemBars())
            metrics.bounds.width() - insets.left - insets.right
        } else {
            val view = window.decorView
            val insets = WindowInsetsCompat.toWindowInsetsCompat(view.rootWindowInsets, view)
                .getInsets(WindowInsetsCompat.Type.systemBars())
            resources.displayMetrics.widthPixels - insets.left - insets.right
        }
    }