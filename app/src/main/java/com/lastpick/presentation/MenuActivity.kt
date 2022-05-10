package com.lastpick.presentation

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lastpick.R
import com.lastpick.presentation.teams.TeamsActivity
import com.lastpick.presentation.teams.TeamsCoroutinesActivity

class MenuActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Surface(color = Color.White) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(
                            onClick = { startActivity(Intent(this@MenuActivity, TeamsCoroutinesActivity::class.java)) },
                            modifier = Modifier.padding(8.dp),
                            contentPadding = PaddingValues(32.dp, 16.dp)
                        ) {
                            Text(text = getString(R.string.hero_choice), fontSize = 20.sp)
                        }
                        Button(
                            onClick = { },
                            modifier = Modifier.padding(8.dp),
                            contentPadding = PaddingValues(32.dp, 16.dp)
                        ) {
                            Text(text = getString(R.string.items_timings), fontSize = 20.sp)
                        }
                    }
                }
            }
        }
    }
}