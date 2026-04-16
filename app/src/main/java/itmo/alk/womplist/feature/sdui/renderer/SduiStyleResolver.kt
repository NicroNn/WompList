package itmo.alk.womplist.feature.sdui.renderer

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import itmo.alk.womplist.core.ui.theme.WompDesignSystem
import itmo.alk.womplist.feature.sdui.ui.UiNode

@Immutable
data class ColumnResolvedStyle(
    val contentPadding: Dp
)

@Immutable
data class HeaderResolvedStyle(
    val textStyle: TextStyle
)

@Immutable
data class SearchResolvedStyle(
    val useFilledField: Boolean
)

class SduiStyleResolver {

    @Composable
    fun resolveColumn(node: UiNode.Column): ColumnResolvedStyle {
        val spacing = WompDesignSystem.tokens.spacing
        val padding = when (node.style.density) {
            "compact" -> spacing.sm
            "comfortable" -> spacing.lg
            else -> spacing.md
        }
        return ColumnResolvedStyle(contentPadding = padding)
    }

    @Composable
    fun resolveHeader(node: UiNode.Header): HeaderResolvedStyle {
        val textStyle = when (node.style.variant) {
            "hero" -> MaterialTheme.typography.headlineMedium
            "subtitle" -> MaterialTheme.typography.titleMedium
            else -> MaterialTheme.typography.headlineSmall
        }
        return HeaderResolvedStyle(textStyle = textStyle)
    }

    @Composable
    fun resolveSearch(node: UiNode.Search): SearchResolvedStyle {
        return SearchResolvedStyle(useFilledField = node.style.variant == "filled")
    }
}

