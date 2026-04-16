package itmo.alk.womplist.core.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.dp

@Immutable
data class WompSpacingTokens(
    val xs: androidx.compose.ui.unit.Dp = 4.dp,
    val sm: androidx.compose.ui.unit.Dp = 8.dp,
    val md: androidx.compose.ui.unit.Dp = 16.dp,
    val lg: androidx.compose.ui.unit.Dp = 24.dp
)

@Immutable
data class WompShapeTokens(
    val sm: androidx.compose.ui.unit.Dp = 8.dp,
    val md: androidx.compose.ui.unit.Dp = 12.dp,
    val lg: androidx.compose.ui.unit.Dp = 20.dp
)

@Immutable
data class WompDesignTokens(
    val spacing: WompSpacingTokens = WompSpacingTokens(),
    val shapes: WompShapeTokens = WompShapeTokens()
)

