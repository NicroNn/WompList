package itmo.alk.womplist.feature.mylist

import android.content.res.Configuration
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
fun MyListScreen(
    viewModel: MyListViewModel
) {
    val state by viewModel.state.collectAsState()

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val languageCode = configuration.locales[0]?.language ?: "en"

    data class StatusData(
        val name: String,
        val status: MyListFilter,
        val icon: @Composable () -> Unit,
        val count: Int
    )

    val statuses = listOf(
        StatusData(
            name = stringResource(R.string.watching),
            status = MyListFilter.WATCHING,
            icon = { Icon(Icons.Default.Visibility, contentDescription = null) },
            count = state.watchingList.size
        ),
        StatusData(
            name = stringResource(R.string.planned),
            status = MyListFilter.PLANNED,
            icon = { Icon(Icons.Default.Schedule, contentDescription = null) },
            count = state.plannedList.size
        ),
        StatusData(
            name = stringResource(R.string.completed),
            status = MyListFilter.COMPLETED,
            icon = { Icon(Icons.Default.CheckCircle, contentDescription = null) },
            count = state.completedList.size
        )
    )

    val selectedStatus = statuses.first { it.status == state.selectedFilter }

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
                text = stringResource(R.string.my_list),
                style = MaterialTheme.typography.headlineSmall
            )
            ScreenCornerAnimation(
                type = ScreenCornerType.MY_LIST,
                onSecretTrigger = { viewModel.onIntent(MyListIntent.TriggerSecret) }
            )
        }

        ExposedDropdownMenuBox(
            expanded = state.isStatusMenuExpanded,
            onExpandedChange = {
                viewModel.onIntent(MyListIntent.ToggleStatusMenu(!state.isStatusMenuExpanded))
            }
        ) {
            OutlinedTextField(
                value = selectedStatus.name,
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = state.isStatusMenuExpanded)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                label = { Text(stringResource(R.string.status)) },
                leadingIcon = selectedStatus.icon,
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
            )
            ExposedDropdownMenu(
                expanded = state.isStatusMenuExpanded,
                onDismissRequest = { viewModel.onIntent(MyListIntent.ToggleStatusMenu(false)) }
            ) {
                statuses.forEach { status ->
                    DropdownMenuItem(
                        text = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(status.name)
                                Badge { Text(status.count.toString()) }
                            }
                        },
                        onClick = {
                            viewModel.onIntent(MyListIntent.SelectFilter(status.status))
                        },
                        leadingIcon = status.icon,
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        state.error?.let { error ->
            ErrorBanner(
                error = error,
                onRetry = { viewModel.onIntent(MyListIntent.Retry) },
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        AnimatedContent(
            targetState = selectedStatus,
            transitionSpec = {
                fadeIn() togetherWith fadeOut() using SizeTransform(clip = false)
            },
            label = "status_content"
        ) { status ->
            if (state.selectedList.isEmpty()) {
                EmptyState(message = stringResource(R.string.no_items_in_list, status.name))
            } else {
                if (isLandscape) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.selectedList) { anime ->
                            AnimeCard(
                                title = preferredAnimeTitle(anime, languageCode),
                                posterUrl = anime.posterUrl,
                                onClick = { viewModel.onIntent(MyListIntent.OpenTitle(anime.id)) },
                                type = AnimeCardType.VERTICAL
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.selectedList) { anime ->
                            AnimeCard(
                                title = preferredAnimeTitle(anime, languageCode),
                                posterUrl = anime.posterUrl,
                                onClick = { viewModel.onIntent(MyListIntent.OpenTitle(anime.id)) },
                                type = AnimeCardType.HORIZONTAL
                            )
                        }
                    }
                }
            }
        }
    }
}