package itmo.alk.womplist.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import itmo.alk.womplist.core.ui.components.ScreenCornerAnimation
import itmo.alk.womplist.core.ui.components.ScreenCornerType
import itmo.alk.womplist.data.LocalAnimeRepository
import itmo.alk.womplist.feature.sdui.actions.ActionHandler
import itmo.alk.womplist.feature.sdui.registry.ComponentRegistry
import itmo.alk.womplist.feature.sdui.renderer.RenderContext
import itmo.alk.womplist.feature.sdui.renderer.RenderNode
import itmo.alk.womplist.feature.sdui.ui.UiDto
import itmo.alk.womplist.feature.sdui.ui.UiNode
import itmo.alk.womplist.feature.sdui.ui.UiParser
import kotlinx.serialization.json.Json

@Composable
fun HomeScreen(
    navController: NavController,
    onSecretTrigger: () -> Unit = {}
) {
    val repository = LocalAnimeRepository.current
    val allAnime by repository.allAnime.collectAsState(initial = emptyList())

    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf(emptyList<itmo.alk.womplist.core.model.Anime>()) }
    var isSearching by remember { mutableStateOf(false) }

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
        searchQuery,
        displayed,
        isSearching,
        onSearchChange = { searchQuery = it },
        onSecretTrigger = onSecretTrigger
    )

    val json = """
    {
      "type": "column",
      "children": [
        {
          "type": "header",
          "props": {
            "title": "Discover Anime",
            "showSecret": true
          }
        },
        {
          "type": "search",
          "props": {
            "hint": "Search anime"
          }
        },
        {
          "type": "anime_list"
        }
      ]
    }
    """

    val parser = remember { UiParser() }

    val rootNode = remember {
        val dto = Json.decodeFromString<UiDto>(json)
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
                    node.children.forEach {
                        RenderNode(it, this@apply, ctx)
                    }
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


    RenderNode(rootNode, registry, ctx)
}