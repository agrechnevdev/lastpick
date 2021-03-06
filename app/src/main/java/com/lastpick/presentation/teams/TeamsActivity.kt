package com.lastpick.presentation.teams

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
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
import com.lastpick.domain.HeroesStorage
import com.lastpick.domain.model.Hero
import com.lastpick.presentation.model.Position
import com.lastpick.presentation.model.Team
import com.lastpick.presentation.model.isFriend
import kotlinx.coroutines.launch
import javax.inject.Inject

class TeamsActivity : ComponentActivity() {

    @Inject
    lateinit var heroInteractor: HeroInteractor

    @Inject
    lateinit var heroesStorage: HeroesStorage

    private lateinit var viewModel: TeamsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (application as BaseApp).appComponent.inject(this)
        val factory = TeamsViewModel.Factory(heroInteractor, heroesStorage)
        viewModel = ViewModelProvider(this, factory).get(TeamsViewModel::class.java)

        setContent {
            ChooseHeroScreen(viewModel = viewModel)
        }

        viewModel.dispatch(TeamsMviAction.ShowLoading)
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun ChooseHeroScreen(viewModel: TeamsViewModel) {
        MaterialTheme {
            Surface(color = Color.White) {
                val viewState = viewModel.observableState.observeAsState()
                when (val screenState = viewState.value?.screenState) {
                    is TeamsMviState.ScreenState.Loading ->
                        FullScreenProgressBar()
                    is TeamsMviState.ScreenState.Error ->
                        ErrorLoading(
                            text = screenState.errorMessage,
                            onClick = { viewModel.dispatch(TeamsMviAction.ShowLoading) }
                        )
                    is TeamsMviState.ScreenState.ScreenShow ->
                        ScreenContent(
                            friendTeam = screenState.friendTeam,
                            enemyTeam = screenState.enemyTeam,
                            bottomSheetOpen = screenState.bottomSheetOpen
                        )
                    else -> {}
                }
            }
        }
    }

    @Composable
    fun FullScreenProgressBar() {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularProgressIndicator(modifier = Modifier.wrapContentSize())
        }
    }

    @Composable
    fun ErrorLoading(@StringRes text: Int?, onClick: () -> Unit) {
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

    @SuppressLint("CoroutineCreationDuringComposition")
    @ExperimentalMaterialApi
    @Composable
    fun ScreenContent(friendTeam: Team, enemyTeam: Team, bottomSheetOpen: Boolean) {
        Row(modifier = Modifier.fillMaxWidth()) {
            TeamColumn(team = friendTeam)
            PositionsColumn()
            TeamColumn(team = enemyTeam)

            val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
                bottomSheetState = BottomSheetState(BottomSheetValue.Collapsed)
            )
            val coroutineScope = rememberCoroutineScope()
            coroutineScope.launch {
                if (bottomSheetOpen) {
                    bottomSheetScaffoldState.bottomSheetState.expand()
                } else {
                    bottomSheetScaffoldState.bottomSheetState.collapse()
                }
            }

            BottomSheetScaffold(
                scaffoldState = bottomSheetScaffoldState,
                sheetContent = {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    ) {
                        Text(text = "Hello from sheet")
                    }
                }, sheetPeekHeight = 0.dp
            ) {

                Button(onClick = {
                    viewModel.dispatch(
                        TeamsMviAction.HeroChosen(
                            Hero(
                                "Antimage",
                                "/apps/dota2/images/dota_react/heroes/antimage.png?"
                            )
                        )
                    )
                }) {
                    Text(text = "Choose Hero")
                }
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
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = alignment
        ) {
            for ((pos, hero) in team.mapHeroes) {
                HeroButton(team = team, pos = pos, hero = hero, onClick = {
                    viewModel.dispatch(TeamsMviAction.ClickHeroButton(team = team, pos = pos))
                })
            }
        }
    }

    @Composable
    fun PositionsColumn() {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.5f),
            verticalArrangement = Arrangement.Top
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
                    Text(
                        text = "${it.pos} " + stringResource(id = R.string.position),
                        fontSize = 8.sp
                    )
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

    override fun onDestroy() {
        viewModel.clear()
        super.onDestroy()
    }
}