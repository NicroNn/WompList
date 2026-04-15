package itmo.alk.womplist.feature.home

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.navigation.NavController
import itmo.alk.womplist.core.ui.components.*

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
        LazyVerticalGrid(columns = GridCells.Fixed(2)) {
            items(state.displayedList) { anime ->
                AnimeCard(
                    title = anime.russianName ?: anime.name,
                    posterUrl = anime.posterUrl,
                    onClick = { navController.navigate("title/${anime.id}") },
                    type = AnimeCardType.VERTICAL
                )
            }
        }
    } else {
        LazyColumn {
            items(state.displayedList) { anime ->
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
