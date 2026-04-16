package itmo.alk.womplist.feature.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import itmo.alk.womplist.core.sdui.HomeSduiServer
import itmo.alk.womplist.data.LocalAnimeRepository
import itmo.alk.womplist.feature.sdui.actions.ActionHandler
import itmo.alk.womplist.feature.sdui.registry.createHomeComponentRegistry
import itmo.alk.womplist.feature.sdui.renderer.RenderContext
import itmo.alk.womplist.feature.sdui.renderer.RenderNode
import itmo.alk.womplist.feature.sdui.renderer.SduiStyleResolver
import itmo.alk.womplist.feature.sdui.ui.UiDto
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
    var rootNode by remember {
        mutableStateOf(
            parser.parse(Json.decodeFromString<UiDto>(sduiServer.initialJson))
        )
    }

    LaunchedEffect(currentJson) {
        runCatching {
            val dto = Json.decodeFromString<UiDto>(currentJson)
            parser.parse(dto)
        }.onSuccess { parsedNode ->
            rootNode = parsedNode
        }
    }

    val styleResolver = remember { SduiStyleResolver() }
    val registry = remember(styleResolver) {
        createHomeComponentRegistry(styleResolver)
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

