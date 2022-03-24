package com.composable

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource

@Composable
fun SimpleImage(@DrawableRes drawable: Int) {
    val image: Painter = painterResource(id = drawable)
    Image(painter = image, contentDescription = "image")
}