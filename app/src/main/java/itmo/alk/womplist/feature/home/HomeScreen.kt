package itmo.alk.womplist.feature.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import itmo.alk.womplist.core.ui.components.ScreenCornerAnimation
import itmo.alk.womplist.core.ui.components.ScreenCornerType
import itmo.alk.womplist.core.sdui.HomeSduiServer
import itmo.alk.womplist.data.LocalAnimeRepository
import itmo.alk.womplist.feature.sdui.actions.ActionHandler
import itmo.alk.womplist.feature.sdui.registry.ComponentRegistry
import itmo.alk.womplist.feature.sdui.renderer.RenderContext
import itmo.alk.womplist.feature.sdui.renderer.RenderNode
import itmo.alk.womplist.feature.sdui.ui.UiDto
import itmo.alk.womplist.feature.sdui.ui.UiNode
import itmo.alk.womplist.feature.sdui.ui.UiParser
import kotlinx.serialization.json.Json
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    navController: NavController,
    onSecretTrigger: () -> Unit = {}
) {
    val repository = LocalAnimeRepository.current
    val allAnime by repository.allAnime.collectAsState(initial = emptyList())

    val scope = rememberCoroutineScope()
    val sduiServer = remember { HomeSduiServer() }

    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf(emptyList<itmo.alk.womplist.core.model.Anime>()) }
    var isSearching by remember { mutableStateOf(false) }
    var currentJson by remember { mutableStateOf(sduiServer.initialJson) }
    var requestGeneration by remember { mutableIntStateOf(0) }
    val listState = rememberLazyListState()
    val gridState = rememberLazyGridState()

    LaunchedEffect(searchQuery) {
        if (searchQuery.isNotBlank()) {
            isSearching = true
            searchResults = repository.searchAnime(searchQuery)
            isSearching = false
        } else {
            searchResults = emptyList()
        }
    }

    val displayed = if (searchQuery.isNotBlank()) searchResults else allAnime

    val state = HomeState(
        searchQuery = searchQuery,
        displayedList = displayed,
        isSearching = isSearching,
        listState = listState,
        gridState = gridState,
        onSearchChange = { searchQuery = it },
        onSecretTrigger = onSecretTrigger,
        onScrollDirectionChange = { direction ->
            requestGeneration += 1
            val currentGeneration = requestGeneration

            scope.launch {
                val response = sduiServer.requestHomeJson(direction)
                if (currentGeneration == requestGeneration) {
                    currentJson = response
                }
            }
        }
    )

    val parser = remember { UiParser() }

    val rootNode = remember(currentJson) {
        val dto = Json.decodeFromString<UiDto>(currentJson)
        parser.parse(dto)
    }

    val registry = remember {
        ComponentRegistry().apply {

            register(UiNode.Column::class.java) { node, ctx ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    RenderColumnChildren(node.children, this@apply, ctx)
                }
            }

            register(UiNode.Header::class.java) { node, ctx ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(node.title)
                    if (node.showSecret) {
                        ScreenCornerAnimation(
                            type = ScreenCornerType.HOME,
                            onSecretTrigger = ctx.state.onSecretTrigger
                        )
                    }
                }
            }

            register(UiNode.Search::class.java) { node, ctx ->
                OutlinedTextField(
                    value = ctx.state.searchQuery,
                    onValueChange = ctx.state.onSearchChange,
                    label = { Text(node.hint) },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            register(UiNode.AnimeList::class.java) { _, ctx ->
                AnimeListRenderer(
                    state = ctx.state,
                    navController = ctx.navController
                )
            }
        }
    }

    val ctx = RenderContext(
        navController = navController,
        state = state,
        actionHandler = ActionHandler(navController)
    )

    Box(modifier = Modifier.fillMaxSize()) {
        RenderNode(rootNode, registry, ctx)
    }
}

@Composable
private fun ColumnScope.RenderColumnChildren(
    children: List<UiNode>,
    registry: ComponentRegistry,
    ctx: RenderContext
) {
    val keyCounters = mutableMapOf<String, Int>()

    val footerSearchChildren = children.filter { child ->
        child is UiNode.Search && (child.weight ?: 0f) > 0f
    }

    if (footerSearchChildren.isEmpty()) {
        children.forEach { child ->
            val childKey = nextRenderKey(child, keyCounters)
            val childWeight = child.weight
            val childModifier = if ((childWeight ?: 0f) > 0f) {
                Modifier
                    .fillMaxWidth()
                    .weight(childWeight!!, fill = true)
            } else {
                Modifier.fillMaxWidth()
            }

            key(childKey) {
                Box(modifier = childModifier) {
                    RenderNode(child, registry, ctx)
                }
            }
        }
        return
    }

    val contentChildren = children.filterNot { child ->
        child is UiNode.Search && (child.weight ?: 0f) > 0f
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            contentChildren.forEach { child ->
                val childKey = nextRenderKey(child, keyCounters)
                val childWeight = child.weight
                val childModifier = if ((childWeight ?: 0f) > 0f) {
                    Modifier
                        .fillMaxWidth()
                        .weight(childWeight!!, fill = true)
                } else {
                    Modifier.fillMaxWidth()
                }

                key(childKey) {
                    Box(modifier = childModifier) {
                        RenderNode(child, registry, ctx)
                    }
                }
            }
        }

        footerSearchChildren.forEach { child ->
            val childKey = nextRenderKey(child, keyCounters)
            key(childKey) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    RenderNode(child, registry, ctx)
                }
            }
        }
    }
}

private fun nextRenderKey(child: UiNode, counters: MutableMap<String, Int>): String {
    val baseKey = when (child) {
        is UiNode.Header -> "header:${child.title}:${child.showSecret}:${child.weight ?: 0f}"
        is UiNode.Search -> "search:${child.hint}:${child.weight ?: 0f}"
        is UiNode.AnimeList -> "anime_list:${child.weight ?: 0f}"
        is UiNode.Column -> "column:${child.weight ?: 0f}:size=${child.children.size}"
    }

    val ordinal = counters.getOrDefault(baseKey, 0)
    counters[baseKey] = ordinal + 1
    return "$baseKey#$ordinal"
}
