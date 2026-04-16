package itmo.alk.womplist.core.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf

val LocalWompDesignTokens = staticCompositionLocalOf { WompDesignTokens() }

object WompDesignSystem {
    val tokens: WompDesignTokens
        @Composable
        @ReadOnlyComposable
        get() = LocalWompDesignTokens.current
}

