package itmo.alk.womplist.feature.sdui.registry

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import itmo.alk.womplist.core.ui.components.ScreenCornerAnimation
import itmo.alk.womplist.core.ui.components.ScreenCornerType
import itmo.alk.womplist.feature.home.AnimeListRenderer
import itmo.alk.womplist.feature.sdui.renderer.RenderContext
import itmo.alk.womplist.feature.sdui.renderer.RenderNode
import itmo.alk.womplist.feature.sdui.renderer.SduiStyleResolver
import itmo.alk.womplist.feature.sdui.ui.UiNode

fun createHomeComponentRegistry(
    styleResolver: SduiStyleResolver = SduiStyleResolver()
): ComponentRegistry {
    return ComponentRegistry().apply {
        register(UiNode.Column::class.java) { node, ctx ->
            val style = styleResolver.resolveColumn(node)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(style.contentPadding)
            ) {
                RenderColumnChildren(node.children, this@apply, ctx)
            }
        }

        register(UiNode.Header::class.java) { node, ctx ->
            val style = styleResolver.resolveHeader(node)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = node.title,
                    style = style.textStyle
                )
                if (node.showSecret) {
                    ScreenCornerAnimation(
                        type = ScreenCornerType.HOME,
                        onSecretTrigger = ctx.state.onSecretTrigger
                    )
                }
            }
        }

        register(UiNode.Search::class.java) { node, ctx ->
            val style = styleResolver.resolveSearch(node)
            if (style.useFilledField) {
                TextField(
                    value = ctx.state.searchQuery,
                    onValueChange = ctx.state.onSearchChange,
                    label = { Text(node.hint) },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            } else {
                OutlinedTextField(
                    value = ctx.state.searchQuery,
                    onValueChange = ctx.state.onSearchChange,
                    label = { Text(node.hint) },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        }

        register(UiNode.AnimeList::class.java) { _, ctx ->
            AnimeListRenderer(
                state = ctx.state,
                navController = ctx.navController
            )
        }
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
        is UiNode.Header -> "header:${child.title}:${child.showSecret}:${child.weight ?: 0f}:${child.style.variant ?: "default"}"
        is UiNode.Search -> "search:${child.hint}:${child.weight ?: 0f}:${child.style.variant ?: "default"}"
        is UiNode.AnimeList -> "anime_list:${child.weight ?: 0f}:${child.style.variant ?: "default"}"
        is UiNode.Column -> "column:${child.weight ?: 0f}:size=${child.children.size}:${child.style.density ?: "regular"}"
    }

    val ordinal = counters.getOrDefault(baseKey, 0)
    counters[baseKey] = ordinal + 1
    return "$baseKey#$ordinal"
}


