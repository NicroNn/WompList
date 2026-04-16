package itmo.alk.womplist.feature.home

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.navigation.NavController
import itmo.alk.womplist.core.ui.components.*
import itmo.alk.womplist.core.sdui.ScrollDirection
import androidx.compose.runtime.snapshotFlow

@Composable
fun AnimeListRenderer(
    state: HomeState,
    navController: NavController
) {
    if (state.isSearching) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    if (state.displayedList.isEmpty()) {
        EmptyState("No results")
        return
    }

    val isLandscape =
        LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    if (isLandscape) {
        ObserveScrollDirection(state.gridState, state.onScrollDirectionChange)

        LazyVerticalGrid(
            state = state.gridState,
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize()
        ) {
            items(state.displayedList, key = { it.id }) { anime ->
                AnimeCard(
                    title = anime.russianName ?: anime.name,
                    posterUrl = anime.posterUrl,
                    onClick = { navController.navigate("title/${anime.id}") },
                    type = AnimeCardType.VERTICAL
                )
            }
        }
    } else {
        ObserveScrollDirection(state.listState, state.onScrollDirectionChange)

        LazyColumn(
            state = state.listState,
            modifier = Modifier.fillMaxSize()
        ) {
            items(state.displayedList, key = { it.id }) { anime ->
                AnimeCard(
                    title = anime.russianName ?: anime.name,
                    posterUrl = anime.posterUrl,
                    onClick = { navController.navigate("title/${anime.id}") },
                    type = AnimeCardType.HORIZONTAL
                )
            }
        }
    }
}

private data class ScrollPosition(
    val index: Int,
    val offset: Int
)

@Composable
private fun ObserveScrollDirection(
    listState: LazyListState,
    onDirectionChange: (ScrollDirection) -> Unit
) {
    LaunchedEffect(listState) {
        var previousPosition: ScrollPosition? = null
        var lastSentDirection: ScrollDirection? = null

        snapshotFlow {
            ScrollPosition(
                index = listState.firstVisibleItemIndex,
                offset = listState.firstVisibleItemScrollOffset
            )
        }.collect { currentPosition ->
            val previous = previousPosition
            previousPosition = currentPosition

            val direction = when {
                previous == null -> null
                currentPosition.index != previous.index -> {
                    if (currentPosition.index > previous.index) ScrollDirection.Down else ScrollDirection.Up
                }

                currentPosition.offset != previous.offset -> {
                    if (currentPosition.offset > previous.offset) ScrollDirection.Down else ScrollDirection.Up
                }

                else -> null
            }

            if (direction != null && direction != lastSentDirection) {
                lastSentDirection = direction
                onDirectionChange(direction)
            }
        }
    }
}

@Composable
private fun ObserveScrollDirection(
    gridState: LazyGridState,
    onDirectionChange: (ScrollDirection) -> Unit
) {
    LaunchedEffect(gridState) {
        var previousPosition: ScrollPosition? = null
        var lastSentDirection: ScrollDirection? = null

        snapshotFlow {
            ScrollPosition(
                index = gridState.firstVisibleItemIndex,
                offset = gridState.firstVisibleItemScrollOffset
            )
        }.collect { currentPosition ->
            val previous = previousPosition
            previousPosition = currentPosition

            val direction = when {
                previous == null -> null
                currentPosition.index != previous.index -> {
                    if (currentPosition.index > previous.index) ScrollDirection.Down else ScrollDirection.Up
                }

                currentPosition.offset != previous.offset -> {
                    if (currentPosition.offset > previous.offset) ScrollDirection.Down else ScrollDirection.Up
                }

                else -> null
            }

            if (direction != null && direction != lastSentDirection) {
                lastSentDirection = direction
                onDirectionChange(direction)
            }
        }
    }
}

