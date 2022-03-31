package com.lastpick.presentation.choosehero

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.lastpick.BaseApp
import com.lastpick.R
import com.lastpick.domain.HeroInteractor
import com.lastpick.domain.model.Hero
import javax.inject.Inject

class ChooseHeroActivity : ComponentActivity() {

    @Inject
    lateinit var heroInteractor: HeroInteractor

    private lateinit var viewModel: ChooseHeroViewModel
    private val friendHeroes = mutableListOf<Hero>()
    private val enemyHeroes = mutableListOf<Hero>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (application as BaseApp).appComponent.inject(this)
        val factory = ChooseHeroViewModel.Factory(null, heroInteractor)
        viewModel = ViewModelProvider(this, factory).get(ChooseHeroViewModel::class.java)

        setContent {
            ChooseHeroScreen()
        }

        viewModel.dispatch(ChooseHeroAction.LoadHeroes)
    }

    @Composable
    fun ChooseHeroScreen() {
        MaterialTheme {
            Surface(color = Color.White) {
                val state =
                    viewModel.observableState.observeAsState(ChooseHeroState(screenStatus = ChooseHeroState.ScreenStatus.Loading)).value
                val screenStatus = (state as ChooseHeroState).screenStatus
                FullScreenProgressBar(visible = screenStatus is ChooseHeroState.ScreenStatus.Loading)
                ErrorLoading(
                    visible = screenStatus is ChooseHeroState.ScreenStatus.Error,
                    text = (screenStatus as? ChooseHeroState.ScreenStatus.Error)?.errorMessage ?: "",
                    onClick = { viewModel.dispatch(ChooseHeroAction.LoadHeroes) }
                )
                ScreenContent(visible = screenStatus is ChooseHeroState.ScreenStatus.Heroes)
            }
        }
    }

    @Composable
    fun FullScreenProgressBar(visible: Boolean) {
        if (visible) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(modifier = Modifier.wrapContentSize())
            }
        }
    }

    @Composable
    fun ErrorLoading(visible: Boolean, text: String, onClick: () -> Unit) {
        if (visible) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = onClick,
                    modifier = Modifier
                        .wrapContentSize()
                        .width(300.dp),
                    contentPadding = PaddingValues(24.dp, 12.dp)
                ) {
                    Text(text = text, fontSize = 16.sp)
                }
            }
        }
    }

    @Composable
    fun ScreenContent(visible: Boolean) {
        if (visible) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Column(
                    modifier = Modifier.fillMaxHeight(),
                    verticalArrangement = Arrangement.Center
                ) {
                    HeroAddButton({})
                    HeroAddButton({})
                    HeroAddButton({})
                    HeroAddButton({})
                    HeroAddButton({})
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Column(
                        modifier = Modifier.fillMaxHeight(),
                        verticalArrangement = Arrangement.Center
                    ) {
                        HeroAddButton({})
                        HeroAddButton({})
                        HeroAddButton({})
                        HeroAddButton({})
                        HeroAddButton({})

                    }
                }
            }
        }
    }

    @Composable
    fun HeroAddButton(onClick: () -> Unit) {
        val roundCorner = 8.dp
        Button(
            modifier = Modifier
                .wrapContentSize()
                .padding(16.dp),
            onClick = onClick,
            shape = RoundedCornerShape(roundCorner)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_add_24),
                contentDescription = null,
                modifier = Modifier.padding(4.dp)
            )
        }
    }

    @Composable
    fun HeroShowButton(hero: Hero, onClick: () -> Unit) {
        Button(onClick = onClick, shape = RoundedCornerShape(2.dp, 2.dp, 2.dp, 2.dp)) {
            Text(text = hero.name, fontSize = 12.sp)
        }
    }
}