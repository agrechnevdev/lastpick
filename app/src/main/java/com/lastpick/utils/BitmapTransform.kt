package com.lastpick.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import com.squareup.picasso.Transformation

/**
 * Трансформация картинки
 *
 *
 * Преобразуем в масштаб экрана и обрезаем справа и снизу
 */
class BitmapTransform(
    private val maxWidth: Int,
    private val maxHeight: Int
) : Transformation {

    override fun transform(source: Bitmap): Bitmap {
        val targetWidth: Int
        val targetHeight: Int = maxHeight
        val aspectRatio: Double = maxWidth.toDouble() / maxHeight.toDouble()
        targetWidth = (targetHeight * aspectRatio).toInt()
        val result = Bitmap.createScaledBitmap(
            source,
            source.width * maxHeight / source.height,
            source.height * maxHeight / source.height,
            false
        )
        val rect = Rect(0, 0, targetWidth, targetHeight)
        val resultBmp = Bitmap.createBitmap(rect.right, rect.bottom, Bitmap.Config.ARGB_8888)
        Canvas(resultBmp).drawBitmap(result, 0f, 0f, null)
        if (resultBmp != source) {
            source.recycle()
        }
        return resultBmp
    }

    override fun key(): String {
        return maxWidth.toString() + "x" + maxHeight
    }
}