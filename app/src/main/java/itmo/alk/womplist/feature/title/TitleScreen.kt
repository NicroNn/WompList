package itmo.alk.womplist.feature.title

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import android.view.MotionEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import itmo.alk.womplist.R
import itmo.alk.womplist.core.model.Anime
import itmo.alk.womplist.core.ui.components.AnimeCard
import itmo.alk.womplist.core.ui.components.AnimeCardType
import itmo.alk.womplist.core.ui.utils.HtmlText
import itmo.alk.womplist.data.repository.AnimeStatus
import kotlinx.coroutines.launch
import kotlin.math.min
import kotlin.math.round
import kotlin.math.roundToInt

private data class RatingFlight(
    val rating: Int,
    val start: Offset,
    val end: Offset
)

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun TitleScreen(
    viewModel: TitleViewModel,
    titleId: Long
) {
    val coroutineScope = rememberCoroutineScope()
    val state by viewModel.state.collectAsState()

    var starCenter by remember { mutableStateOf(Offset.Zero) }
    var flyingRating by remember { mutableStateOf<RatingFlight?>(null) }

    LaunchedEffect(titleId) {
        viewModel.onIntent(TitleIntent.Initialize(titleId))
    }

    if (state.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val animeData = state.anime
    if (animeData == null) {
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned {
                if (starCenter == Offset.Zero) {
                    starCenter = Offset(it.size.width.toFloat() - 24f, 24f)
                }
            }
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            ) {
                AsyncImage(
                    model = animeData.posterUrl,
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
                        text = animeData.russianName ?: animeData.name,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Box {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = stringResource(R.string.rate_title),
                                modifier = Modifier
                                    .size(16.dp)
                                    .onGloballyPositioned { coordinates ->
                                        val position = coordinates.positionInRoot()
                                        starCenter = Offset(
                                            x = position.x + coordinates.size.width / 2f,
                                            y = position.y + coordinates.size.height / 2f
                                        )
                                    }
                                    .combinedClickable(
                                        onClick = {},
                                        onLongClick = { viewModel.onIntent(TitleIntent.OpenRatingOverlay) }
                                    ),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("${"%.1f".format(animeData.score)}/10")
                        if (state.currentUserRating != null) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.your_rating, state.currentUserRating!!),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(animeData.year?.toString() ?: "?")
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
                    onClick = { viewModel.onIntent(TitleIntent.OpenStatusDialog) },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        when (state.currentStatus) {
                            AnimeStatus.WATCHING -> Icons.Default.Visibility
                            AnimeStatus.PLANNED -> Icons.Default.Schedule
                            AnimeStatus.COMPLETED -> Icons.Default.CheckCircle
                            else -> Icons.Default.Add
                        },
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        when (state.currentStatus) {
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
                    0 -> AboutTab(animeData)
                    1 -> EpisodesTab(animeData)
                    2 -> RecommendationsTab(
                        recommendations = state.recommendations,
                        onAnimeClick = { id -> viewModel.onIntent(TitleIntent.OpenTitle(id)) }
                    )
                }
            }
        }

        flyingRating?.let { flight ->
            FlyingRatingBadge(
                flight = flight,
                modifier = Modifier.align(Alignment.TopStart),
                onAnimationFinished = { rating ->
                    viewModel.onIntent(TitleIntent.SaveRating(rating))
                    flyingRating = null
                }
            )
        }

        if (state.isRatingOverlayVisible) {
            RatingOverlay(
                initialRating = state.currentUserRating ?: 8,
                onDismiss = { viewModel.onIntent(TitleIntent.DismissRatingOverlay) },
                onRatingConfirmed = { rating, sourcePoint ->
                    flyingRating = RatingFlight(
                        rating = rating,
                        start = sourcePoint,
                        end = starCenter
                    )
                    viewModel.onIntent(TitleIntent.DismissRatingOverlay)
                }
            )
        }
    }

    if (state.isStatusDialogVisible) {
        AlertDialog(
            onDismissRequest = { viewModel.onIntent(TitleIntent.DismissStatusDialog) },
            title = { Text(stringResource(R.string.select_status)) },
            text = {
                Column {
                    StatusOption(
                        label = stringResource(R.string.watching),
                        icon = Icons.Default.Visibility,
                        onClick = {
                            viewModel.onIntent(TitleIntent.SetStatus(AnimeStatus.WATCHING))
                        }
                    )
                    StatusOption(
                        label = stringResource(R.string.planned),
                        icon = Icons.Default.Schedule,
                        onClick = {
                            viewModel.onIntent(TitleIntent.SetStatus(AnimeStatus.PLANNED))
                        }
                    )
                    StatusOption(
                        label = stringResource(R.string.completed),
                        icon = Icons.Default.CheckCircle,
                        onClick = {
                            viewModel.onIntent(TitleIntent.SetStatus(AnimeStatus.COMPLETED))
                        }
                    )
                    if (state.currentStatus != null) {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        StatusOption(
                            label = stringResource(R.string.remove_from_list),
                            icon = Icons.Default.Delete,
                            onClick = {
                                viewModel.onIntent(TitleIntent.RemoveFromList)
                            },
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { viewModel.onIntent(TitleIntent.DismissStatusDialog) }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

@Composable
private fun RatingOverlay(
    initialRating: Int,
    onDismiss: () -> Unit,
    onRatingConfirmed: (Int, Offset) -> Unit
) {
    val haptic = LocalHapticFeedback.current
    var currentStars by remember(initialRating) {
        mutableStateOf((initialRating.coerceIn(1, 10) / 2f).coerceIn(0.5f, 5f))
    }
    var rowOrigin by remember { mutableStateOf(Offset.Zero) }
    var rowWidthPx by remember { mutableStateOf(1f) }
    var rowCenterY by remember { mutableStateOf(0f) }

    fun updateStarsByX(localX: Float) {
        val normalized = (localX / rowWidthPx).coerceIn(0f, 1f)
        val stepped = round(normalized * 10f) / 2f
        val newStars = stepped.coerceIn(0.5f, 5f)
        if (newStars != currentStars) {
            currentStars = newStars
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.55f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onDismiss
            ),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shape = MaterialTheme.shapes.large,
            tonalElevation = 8.dp,
            modifier = Modifier
                .padding(24.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {}
                )
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.rate_title),
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .onGloballyPositioned { coordinates ->
                            val position = coordinates.positionInRoot()
                            rowOrigin = position
                            rowWidthPx = coordinates.size.width.toFloat().coerceAtLeast(1f)
                            rowCenterY = position.y + (coordinates.size.height / 2f)
                        }
                        .pointerInteropFilter { event ->
                            when (event.actionMasked) {
                                MotionEvent.ACTION_DOWN,
                                MotionEvent.ACTION_MOVE -> {
                                    updateStarsByX(event.x.coerceIn(0f, rowWidthPx))
                                    true
                                }

                                MotionEvent.ACTION_UP -> {
                                    val clampedX = event.x.coerceIn(0f, rowWidthPx)
                                    updateStarsByX(clampedX)
                                    val sourcePoint = Offset(rowOrigin.x + clampedX, rowCenterY)
                                    onRatingConfirmed(
                                        (currentStars * 2).roundToInt().coerceIn(1, 10),
                                        sourcePoint
                                    )
                                    true
                                }

                                MotionEvent.ACTION_CANCEL -> {
                                    onDismiss()
                                    true
                                }

                                else -> false
                            }
                        },
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (starIndex in 1..5) {
                        val icon = when {
                            currentStars >= starIndex -> Icons.Default.Star
                            currentStars >= starIndex - 0.5f -> Icons.AutoMirrored.Filled.StarHalf
                            else -> Icons.Default.StarOutline
                        }
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = stringResource(R.string.your_rating, (currentStars * 2).roundToInt()),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.rating_overlay_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun FlyingRatingBadge(
    flight: RatingFlight,
    modifier: Modifier = Modifier,
    onAnimationFinished: (Int) -> Unit
) {
    val progress = remember(flight) { Animatable(0f) }

    LaunchedEffect(flight) {
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing)
        )
        onAnimationFinished(flight.rating)
    }

    val currentProgress = progress.value
    val controlPoint = Offset(
        x = (flight.start.x + flight.end.x) / 2f,
        y = min(flight.start.y, flight.end.y) - 180f
    )
    val currentPosition = quadraticBezier(
        start = flight.start,
        control = controlPoint,
        end = flight.end,
        progress = currentProgress
    )

    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.primary,
        tonalElevation = 6.dp,
        modifier = modifier
            .offset {
                IntOffset(
                    x = (currentPosition.x - 22f).roundToInt(),
                    y = (currentPosition.y - 14f).roundToInt()
                )
            }
            .graphicsLayer {
                val scale = 1f + 0.25f * (1f - currentProgress)
                scaleX = scale
                scaleY = scale
            }
    ) {
        Text(
            text = flight.rating.toString(),
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

private fun quadraticBezier(start: Offset, control: Offset, end: Offset, progress: Float): Offset {
    val oneMinusT = 1f - progress
    return Offset(
        x = oneMinusT * oneMinusT * start.x + 2f * oneMinusT * progress * control.x + progress * progress * end.x,
        y = oneMinusT * oneMinusT * start.y + 2f * oneMinusT * progress * control.y + progress * progress * end.y
    )
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
        HtmlText(anime.descriptionHtml)
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
        items(anime.episodes) { episodeNumber ->
            androidx.compose.material3.ListItem(
                headlineContent = { Text("Эпизод $episodeNumber") },
                leadingContent = { Icon(Icons.Default.PlayArrow, null) }
            )
        }
    }
}

@Composable
fun RecommendationsTab(
    recommendations: List<Anime>,
    onAnimeClick: (Long) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(recommendations) { anime ->
            AnimeCard(
                title = anime.russianName ?: anime.name,
                posterUrl = anime.posterUrl,
                onClick = { onAnimeClick(anime.id) },
                type = AnimeCardType.HORIZONTAL
            )
        }
    }
}