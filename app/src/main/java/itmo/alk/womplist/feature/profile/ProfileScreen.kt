package itmo.alk.womplist.feature.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalConfiguration
import itmo.alk.womplist.R
import itmo.alk.womplist.core.ui.components.AnimeCard
import itmo.alk.womplist.core.ui.components.AnimeCardType
import itmo.alk.womplist.core.ui.components.EmptyState
import itmo.alk.womplist.core.ui.components.ErrorBanner
import itmo.alk.womplist.core.ui.components.ScreenCornerAnimation
import itmo.alk.womplist.core.ui.components.ScreenCornerType
import itmo.alk.womplist.core.ui.utils.preferredAnimeTitle

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel
) {
    val state by viewModel.state.collectAsState()
    val languageCode = LocalConfiguration.current.locales[0]?.language ?: "en"

    val stats = listOf(
        Triple(stringResource(R.string.watching), state.watchingList.size, Icons.Default.Visibility),
        Triple(stringResource(R.string.completed), state.completedList.size, Icons.Default.CheckCircle),
        Triple(stringResource(R.string.planned), state.plannedList.size, Icons.Default.Schedule)
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = state.username,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(
                        text = stringResource(R.string.member_since, "2024"),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ScreenCornerAnimation(
                        type = ScreenCornerType.PROFILE,
                        onSecretTrigger = { viewModel.onIntent(ProfileIntent.TriggerSecret) }
                    )
                    IconButton(onClick = { viewModel.onIntent(ProfileIntent.OpenSettings) }) {
                        Icon(Icons.Default.Settings, contentDescription = stringResource(R.string.settings))
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    stats.forEach { (label, count, icon) ->
                        ProfileStatItem(label = label, count = count, icon = icon)
                    }
                }
            }
        }

        item {
            Text(
                text = stringResource(R.string.continue_watching),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        state.error?.let { error ->
            item {
                ErrorBanner(
                    error = error,
                    onRetry = { viewModel.onIntent(ProfileIntent.Retry) }
                )
            }
        }

        if (state.continueWatching.isNotEmpty()) {
            items(state.continueWatching) { anime ->
                AnimeCard(
                    title = preferredAnimeTitle(anime, languageCode),
                    posterUrl = anime.posterUrl,
                    onClick = { viewModel.onIntent(ProfileIntent.OpenTitle(anime.id)) },
                    type = AnimeCardType.HORIZONTAL
                )
            }
        } else {
            item {
                EmptyState(message = stringResource(R.string.nothing_to_continue))
            }
        }
    }
}

@Composable
fun ProfileStatItem(label: String, count: Int, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(24.dp))
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}