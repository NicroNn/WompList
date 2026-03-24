package itmo.alk.womplist.core.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class ScreenCornerType {
    HOME,
    MY_LIST,
    PROFILE
}

@Composable
fun ScreenCornerAnimation(
    type: ScreenCornerType,
    onSecretTrigger: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val tapTimestamps = remember { ArrayDeque<Long>() }
    val burstWindowMs = EasterEggConfig.BURST_WINDOW_MS
    val tapsForBurst = EasterEggConfig.TAPS_FOR_BURST
    val holdDurationMs = EasterEggConfig.HOLD_DURATION_MS

    fun registerTap() {
        val now = System.currentTimeMillis()
        tapTimestamps.addLast(now)
        while (tapTimestamps.isNotEmpty() && now - tapTimestamps.first() > burstWindowMs) {
            tapTimestamps.removeFirst()
        }
        if (tapTimestamps.size >= tapsForBurst) {
            tapTimestamps.clear()
            onSecretTrigger()
        }
    }

    val transition = rememberInfiniteTransition(label = "corner_animation")
    val bobOffset by transition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1300, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "corner_bob"
    )
    val iconRotation by transition.animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "corner_rotation"
    )

    val icon = when (type) {
        ScreenCornerType.HOME -> Icons.Default.Whatshot
        ScreenCornerType.MY_LIST -> Icons.Default.CheckCircle
        ScreenCornerType.PROFILE -> Icons.Default.Person
    }

    Box(
        modifier = modifier
            .size(44.dp)
            .pointerInput(onSecretTrigger) {
                detectTapGestures(
                    onPress = {
                        var triggeredByHold = false
                        val holdJob = scope.launch {
                            delay(holdDurationMs)
                            triggeredByHold = true
                            tapTimestamps.clear()
                            onSecretTrigger()
                        }

                        val released = tryAwaitRelease()
                        holdJob.cancel()

                        if (released && !triggeredByHold) {
                            registerTap()
                        }
                    }
                )
            }
            .graphicsLayer {
                translationY = bobOffset
            }
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.75f)),
        contentAlignment = Alignment.Center
    ) {
        CornerIcon(icon = icon, rotation = iconRotation)
    }
}

@Composable
private fun CornerIcon(icon: ImageVector, rotation: Float) {
    Icon(
        imageVector = icon,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.onPrimaryContainer,
        modifier = Modifier.graphicsLayer {
            rotationZ = rotation
        }
    )
}

