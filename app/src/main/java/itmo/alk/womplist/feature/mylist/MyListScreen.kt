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
import androidx.navigation.NavController
import itmo.alk.womplist.R
import itmo.alk.womplist.core.model.Anime
import itmo.alk.womplist.core.ui.components.AnimeCard
import itmo.alk.womplist.core.ui.components.AnimeCardType
import itmo.alk.womplist.core.ui.components.EmptyState
import itmo.alk.womplist.core.ui.components.ScreenCornerAnimation
import itmo.alk.womplist.core.ui.components.ScreenCornerType
import itmo.alk.womplist.data.LocalAnimeRepository
import itmo.alk.womplist.data.repository.AnimeStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyListScreen(
    navController: NavController,
    onSecretTrigger: () -> Unit = {}
) {
    val repository = LocalAnimeRepository.current

    val watchingList by repository.watchingList.collectAsState(initial = emptyList())
    val plannedList by repository.plannedList.collectAsState(initial = emptyList())
    val completedList by repository.completedList.collectAsState(initial = emptyList())

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    data class StatusData(
        val name: String,
        val status: AnimeStatus,
        val icon: @Composable () -> Unit,
        val count: Int,
        val list: List<Anime>
    )

    val statuses = listOf(
        StatusData(
            name = stringResource(R.string.watching),
            status = AnimeStatus.WATCHING,
            icon = { Icon(Icons.Default.Visibility, contentDescription = null) },
            count = watchingList.size,
            list = watchingList
        ),
        StatusData(
            name = stringResource(R.string.planned),
            status = AnimeStatus.PLANNED,
            icon = { Icon(Icons.Default.Schedule, contentDescription = null) },
            count = plannedList.size,
            list = plannedList
        ),
        StatusData(
            name = stringResource(R.string.completed),
            status = AnimeStatus.COMPLETED,
            icon = { Icon(Icons.Default.CheckCircle, contentDescription = null) },
            count = completedList.size,
            list = completedList
        )
    )

    var expanded by remember { mutableStateOf(false) }
    var selectedStatus by remember { mutableStateOf(statuses[0]) }

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
                onSecretTrigger = onSecretTrigger
            )
        }

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedStatus.name,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                label = { Text(stringResource(R.string.status)) },
                leadingIcon = selectedStatus.icon,
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
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
                            selectedStatus = status
                            expanded = false
                        },
                        leadingIcon = status.icon,
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        AnimatedContent(
            targetState = selectedStatus,
            transitionSpec = {
                fadeIn() togetherWith fadeOut() using SizeTransform(clip = false)
            },
            label = "status_content"
        ) { status ->
            if (status.list.isEmpty()) {
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
                        items(status.list) { anime ->
                            AnimeCard(
                                title = anime.russianName ?: anime.name,
                                posterUrl = anime.posterUrl,
                                onClick = { navController.navigate("title/${anime.id}") },
                                type = AnimeCardType.VERTICAL
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(status.list) { anime ->
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
        }
    }
}