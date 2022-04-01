package com.lastpick.presentation.choosehero

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
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
import androidx.compose.material.ButtonDefaults
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.lastpick.BaseApp
import com.lastpick.R
import com.lastpick.domain.HeroInteractor
import com.lastpick.domain.model.Hero
import com.lastpick.presentation.model.Position
import com.lastpick.presentation.model.Team
import com.lastpick.presentation.model.isFriend
import javax.inject.Inject

class ChooseHeroActivity : ComponentActivity() {

    @Inject
    lateinit var heroInteractor: HeroInteractor

    private lateinit var viewModel: ChooseHeroViewModel

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
                    viewModel.observableState.observeAsState(ChooseHeroState(screenState = ChooseHeroState.ScreenState.Loading)).value
                val screenStatus = state.screenState
                FullScreenProgressBar(visible = screenStatus is ChooseHeroState.ScreenState.Loading)
                ErrorLoading(
                    visible = screenStatus is ChooseHeroState.ScreenState.Error,
                    text = (screenStatus as? ChooseHeroState.ScreenState.Error)?.errorMessage,
                    onClick = { viewModel.dispatch(ChooseHeroAction.LoadHeroes) }
                )
                ScreenContent(
                    visible = screenStatus is ChooseHeroState.ScreenState.Heroes,
                    listHeroes = (screenStatus as ChooseHeroState.ScreenState.Heroes).listHeroes
                )
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
    fun ErrorLoading(visible: Boolean, @StringRes text: Int?, onClick: () -> Unit) {
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
                    Text(text = stringResource(text ?: 0), fontSize = 16.sp)
                }
            }
        }
    }

    @Composable
    fun ScreenContent(visible: Boolean, listHeroes: List<Hero>) {
        if (visible) {
            Row(modifier = Modifier.fillMaxWidth()) {
                TeamColumn(team = Team.FriendTeam)
                PositionsColumn()
                TeamColumn(team = Team.EnemyTeam)
            }
        }
    }

    @Composable
    fun TeamColumn(team: Team) {
        val alignment = if (team.isFriend()) Alignment.Start else Alignment.End
        // val color = if (team.isFriend()) Color.Green else Color.Red
        val fraction = if (team.isFriend()) 0.3333f else 1f
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(fraction),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = alignment
        ) {
            for ((pos, hero) in team.mapHeroes) {
                HeroButton(team = team, pos = pos, hero = hero, onClick = {})
            }
        }
    }

    @Composable
    fun PositionsColumn() {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.5f),
            verticalArrangement = Arrangement.Center
        ) {
            Position.values().forEach {
                Button(
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(16.dp),
                    onClick = {},
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent)
                ) {
                    Text(text = "${it.pos} " + stringResource(id = R.string.position), fontSize = 8.sp)
                }
            }
        }
    }

    @Composable
    fun HeroButton(team: Team, pos: Position, hero: Hero?, onClick: () -> Unit) {
        if (hero == null) {
            HeroAddButton(team = team, onClick = onClick)
        } else {
            HeroShowButton(hero = hero, onClick = onClick)
        }
    }

    @Composable
    fun HeroShowButton(hero: Hero, onClick: () -> Unit) {
        Button(onClick = onClick, shape = RoundedCornerShape(2.dp, 2.dp, 2.dp, 2.dp)) {
            Text(text = hero.name, fontSize = 12.sp)
        }
    }

    @Composable
    fun HeroAddButton(team: Team, onClick: () -> Unit) {
        val color = if (team.isFriend()) Color.Green else Color.Red
        Button(
            modifier = Modifier
                .wrapContentSize()
                .padding(16.dp),
            onClick = onClick,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = color)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_add_24),
                contentDescription = null,
                modifier = Modifier.padding(4.dp)
            )

        }
    }
}