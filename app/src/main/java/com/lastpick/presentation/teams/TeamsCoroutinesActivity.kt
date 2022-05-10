package com.lastpick.presentation.teams

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.composable.ErrorLoading
import com.composable.FullScreenProgressBar
import com.lastpick.BaseApp
import com.lastpick.R
import com.lastpick.domain.HeroInteractor
import com.lastpick.domain.HeroesStorage
import com.lastpick.domain.model.Hero
import com.lastpick.presentation.model.Position
import com.lastpick.presentation.model.Team
import com.lastpick.presentation.model.isFriend
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class TeamsCoroutinesActivity : ComponentActivity() {

    @Inject
    lateinit var heroInteractor: HeroInteractor

    @Inject
    lateinit var heroesStorage: HeroesStorage

    private lateinit var viewModel: TeamsCoroutinesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (application as BaseApp).appComponent.inject(this)
        val factory = TeamsCoroutinesViewModel.Factory(heroInteractor, heroesStorage)
        viewModel = ViewModelProvider(this, factory).get(TeamsCoroutinesViewModel::class.java)

        setContent {
            ChooseHeroScreen(viewModel = viewModel)
        }
        viewModel.doIntent(TeamsMviIntent.ReloadHeroes)
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun ChooseHeroScreen(viewModel: TeamsCoroutinesViewModel) {
        val viewState by viewModel.stateFlow.collectAsState()

//        LaunchedEffect(viewModel.stateFlow) {
//            viewModel.intentChannel
//                .consumeAsFlow()
//                .onStart { emit(TeamsMviIntent.ReloadHeroes) }
//                .collect()
//        }

        MaterialTheme {
            Surface(color = Color.White) {
                when (val screenState = viewState.screenState) {
                    is TeamsMviState.ScreenState.Loading ->
                        FullScreenProgressBar()
                    is TeamsMviState.ScreenState.Error ->
                        ErrorLoading(
                            text = screenState.errorMessage,
                            onClick = { viewModel.doIntent(TeamsMviIntent.ReloadHeroes) }
                        )
                    is TeamsMviState.ScreenState.ScreenShow ->
                        ScreenContent(
                            friendTeam = screenState.friendTeam,
                            enemyTeam = screenState.enemyTeam,
                            bottomSheetOpen = screenState.bottomSheetOpen
                        )
                }
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
//
//            val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
//                bottomSheetState = BottomSheetState(BottomSheetValue.Collapsed)
//            )
//            val coroutineScope = rememberCoroutineScope()
//            coroutineScope.launch {
//                if (bottomSheetOpen) {
//                    bottomSheetScaffoldState.bottomSheetState.expand()
//                } else {
//                    bottomSheetScaffoldState.bottomSheetState.collapse()
//                }
//            }
//
//            BottomSheetScaffold(
//                scaffoldState = bottomSheetScaffoldState,
//                sheetContent = {
//                    Box(
//                        Modifier
//                            .fillMaxWidth()
//                            .height(200.dp)
//                    ) {
//                        Text(text = "Hello from sheet")
//                    }
//                }, sheetPeekHeight = 0.dp
//            ) {
//
//                Button(onClick = {
//                    viewModel.doIntent(
//                        TeamsMviIntent.HeroChosen(
//                            Hero(
//                                "Antimage",
//                                "/apps/dota2/images/dota_react/heroes/antimage.png?"
//                            )
//                        )
//                    )
//                }) {
//                    Text(text = "Choose Hero")
//                }
//            }
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
                    viewModel.doIntent(TeamsMviIntent.ClickHeroButton(team = team, pos = pos))
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

        super.onDestroy()
    }
}