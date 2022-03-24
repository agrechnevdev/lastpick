package com.lastpick.presentation.start

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.composable.SimpleImage
import com.lastpick.R
import com.lastpick.presentation.MainActivity

class StartActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Box() {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        SimpleImage(R.drawable.prelogin_background)
                    }
                }
            }
        }

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this@StartActivity, MainActivity::class.java))
            overridePendingTransition(0, 0)
            finish()
        }, SPLASH_TIME_OUT.toLong())
    }

    private companion object {
        private const val SPLASH_TIME_OUT = 2500
    }
}