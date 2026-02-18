package itmo.alk.womplist.feature.title

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.protobuf.LazyStringArrayList.emptyList
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import itmo.alk.womplist.R
import itmo.alk.womplist.core.model.Anime
import itmo.alk.womplist.core.ui.components.AnimeCard
import itmo.alk.womplist.core.ui.components.AnimeCardType
import itmo.alk.womplist.data.LocalAnimeRepository
import itmo.alk.womplist.data.repository.AnimeRepository
import itmo.alk.womplist.data.repository.AnimeStatus
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlin.collections.emptyList
import androidx.compose.runtime.collectAsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TitleScreen(
    navController: NavController,
    titleId: Long
) {
    val repository = LocalAnimeRepository.current
    val coroutineScope = rememberCoroutineScope()

    val anime = repository.getAnimeById(titleId)
    Log.d("TitleScreen", "anime = $anime")

    var showStatusDialog by remember { mutableStateOf(false) }
    val currentStatus = repository.getStatusForAnime(titleId)

    if (anime == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Anime not found")
        }
        return
    }

    val pagerState = rememberPagerState(pageCount = { 3 })
    val tabs = listOf(
        stringResource(R.string.about),
        stringResource(R.string.episodes),
        stringResource(R.string.recommendations)
    )

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
        ) {
            Image(
                painter = painterResource(id = anime.posterResId),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, MaterialTheme.colorScheme.background),
                            startY = 150f
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Text(
                    text = anime.title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${anime.rating}/10")
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("${anime.year}")
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = { /* Запуск видео */ },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.watch))
            }
            OutlinedButton(
                onClick = { showStatusDialog = true },
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    when (currentStatus) {
                        AnimeStatus.WATCHING -> Icons.Default.Visibility
                        AnimeStatus.PLANNED -> Icons.Default.Schedule
                        AnimeStatus.COMPLETED -> Icons.Default.CheckCircle
                        else -> Icons.Default.Add
                    },
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    when (currentStatus) {
                        AnimeStatus.WATCHING -> stringResource(R.string.in_watching)
                        AnimeStatus.PLANNED -> stringResource(R.string.in_planned)
                        AnimeStatus.COMPLETED -> stringResource(R.string.in_completed)
                        else -> stringResource(R.string.add_to_list)
                    }
                )
            }
        }

        TabRow(
            selectedTabIndex = pagerState.currentPage,
            modifier = Modifier.fillMaxWidth()
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    text = { Text(title) }
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            when (page) {
                0 -> AboutTab(anime)
                1 -> EpisodesTab(anime)
                2 -> RecommendationsTab(navController, repository)
            }
        }
    }

    if (showStatusDialog) {
        AlertDialog(
            onDismissRequest = { showStatusDialog = false },
            title = { Text(stringResource(R.string.select_status)) },
            text = {
                Column {
                    StatusOption(
                        label = stringResource(R.string.watching),
                        icon = Icons.Default.Visibility,
                        onClick = {
                            coroutineScope.launch {
                                repository.addToList(anime, AnimeStatus.WATCHING)
                            }
                            showStatusDialog = false
                        }
                    )
                    StatusOption(
                        label = stringResource(R.string.planned),
                        icon = Icons.Default.Schedule,
                        onClick = {
                            coroutineScope.launch {
                                repository.addToList(anime, AnimeStatus.PLANNED)
                            }
                            showStatusDialog = false
                        }
                    )
                    StatusOption(
                        label = stringResource(R.string.completed),
                        icon = Icons.Default.CheckCircle,
                        onClick = {
                            coroutineScope.launch {
                                repository.addToList(anime, AnimeStatus.COMPLETED)
                            }
                            showStatusDialog = false
                        }
                    )
                    if (currentStatus != null) {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        StatusOption(
                            label = stringResource(R.string.remove_from_list),
                            icon = Icons.Default.Delete,
                            onClick = {
                                coroutineScope.launch {
                                    repository.removeFromList(anime.id, currentStatus)
                                }
                                showStatusDialog = false
                            },
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showStatusDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

@Composable
fun StatusOption(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    color: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = color)
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = label, color = color)
    }
}

@Composable
fun AboutTab(anime: Anime) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.synopsis),
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(anime.description)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.genres),
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(anime.genres.joinToString(", "))
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.episodes_count, anime.episodes),
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(anime.status)
    }
}

@Composable
fun EpisodesTab(anime: Anime) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(anime.episodesList) { episode ->
            ListItem(
                headlineContent = { Text(episode) },
                leadingContent = { Icon(Icons.Default.PlayArrow, null) }
            )
        }
    }
}

@Composable
fun RecommendationsTab(
    navController: NavController,
    repository: AnimeRepository
) {
    val recommendations = repository.allAnime.collectAsState().value
    val recs = recommendations.take(3)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(recs) { anime : Anime ->
            AnimeCard(
                title = anime.title,
                onClick = { navController.navigate("title/${anime.id}") },
                type = AnimeCardType.HORIZONTAL
            )
        }
    }
}


@Preview
@Composable
fun TitleScreenPreview() {
    TitleScreen(navController = rememberNavController(), titleId = 1L)
}
