package itmo.alk.womplist.feature.home

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import itmo.alk.womplist.R
import itmo.alk.womplist.core.ui.components.AnimeCard
import itmo.alk.womplist.core.ui.components.AnimeCardType
import itmo.alk.womplist.core.ui.components.EmptyState
import itmo.alk.womplist.core.ui.components.ErrorBanner
import itmo.alk.womplist.core.ui.components.ScreenCornerAnimation
import itmo.alk.womplist.core.ui.components.ScreenCornerType
import itmo.alk.womplist.core.ui.utils.preferredAnimeTitle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel
) {
    val state by viewModel.state.collectAsState()
    val languageCode = LocalConfiguration.current.locales[0]?.language ?: "en"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.discover_anime),
                style = MaterialTheme.typography.headlineSmall
            )
            ScreenCornerAnimation(
                type = ScreenCornerType.HOME,
                onSecretTrigger = { viewModel.onIntent(HomeIntent.TriggerSecret) }
            )
        }

        OutlinedTextField(
            value = state.searchQuery,
            onValueChange = { viewModel.onIntent(HomeIntent.SearchChanged(it)) },
            label = { Text(stringResource(R.string.search_hint)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        state.error?.let { error ->
            ErrorBanner(
                error = error,
                onRetry = { viewModel.onIntent(HomeIntent.Retry) },
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        if (state.isSearching) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (state.displayedList.isEmpty()) {
            EmptyState(message = stringResource(R.string.no_results))
        } else {
            val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
            if (isLandscape) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.displayedList) { anime ->
                        AnimeCard(
                            title = preferredAnimeTitle(anime, languageCode),
                            posterUrl = anime.posterUrl,
                            onClick = { viewModel.onIntent(HomeIntent.OpenTitle(anime.id)) },
                            type = AnimeCardType.VERTICAL
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.displayedList) { anime ->
                        AnimeCard(
                            title = preferredAnimeTitle(anime, languageCode),
                            posterUrl = anime.posterUrl,
                            onClick = { viewModel.onIntent(HomeIntent.OpenTitle(anime.id)) },
                            type = AnimeCardType.HORIZONTAL
                        )
                    }
                }
            }
        }
    }
}