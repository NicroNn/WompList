package itmo.alk.womplist.core.ui.components

import android.os.Build
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import kotlin.math.roundToInt
import kotlin.random.Random

@Composable
fun BouncingEasterEggOverlay(
    triggerKey: Int,
    assetSpec: EasterEggAssetSpec,
    modifier: Modifier = Modifier,
    onFinished: () -> Unit
) {
    if (triggerKey <= 0) return

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val context = LocalContext.current
        val density = LocalDensity.current
        val ballSize = 84.dp
        val ballSizePx = with(density) { ballSize.toPx() }
        val maxX = with(density) { maxWidth.toPx() }
        val maxY = with(density) { maxHeight.toPx() }
        val imageLoader = remember(context) {
            ImageLoader.Builder(context)
                .components {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        add(ImageDecoderDecoder.Factory())
                    } else {
                        add(GifDecoder.Factory())
                    }
                }
                .build()
        }

        var x by remember(triggerKey) { mutableFloatStateOf((maxX - ballSizePx) / 2f) }
        var y by remember(triggerKey) { mutableFloatStateOf(-ballSizePx * 1.2f) }
        var vx by remember(triggerKey) { mutableFloatStateOf(Random.nextFloat() * 1000f - 500f) }
        var vy by remember(triggerKey) { mutableFloatStateOf(150f) }
        var rotation by remember(triggerKey) { mutableFloatStateOf(0f) }
        var show by remember(triggerKey) { mutableStateOf(false) }
        var forceFallback by remember(triggerKey) { mutableStateOf(assetSpec.gifResId == null) }

        val animationResId = if (forceFallback) {
            assetSpec.fallbackDrawableResId
        } else {
            assetSpec.gifResId ?: assetSpec.fallbackDrawableResId
        }

        val animatedAlpha by animateFloatAsState(
            targetValue = if (show) 1f else 0f,
            animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing),
            label = "easter_egg_alpha"
        )

        LaunchedEffect(triggerKey, maxX, maxY) {
            if (maxX <= 0f || maxY <= 0f) return@LaunchedEffect

            show = true
            val gravity = EasterEggConfig.GRAVITY
            val restitution = EasterEggConfig.RESTITUTION
            val ttlMs = EasterEggConfig.OVERLAY_TTL_MS
            val start = System.currentTimeMillis()
            var previousFrame = 0L

            while (System.currentTimeMillis() - start < ttlMs) {
                withFrameNanos { frameTimeNanos ->
                    if (previousFrame == 0L) {
                        previousFrame = frameTimeNanos
                        return@withFrameNanos
                    }

                    val dt = ((frameTimeNanos - previousFrame) / 1_000_000_000f).coerceAtMost(0.033f)
                    previousFrame = frameTimeNanos

                    vy += gravity * dt
                    x += vx * dt
                    y += vy * dt
                    rotation += vx * dt * 0.05f

                    val rightBound = (maxX - ballSizePx).coerceAtLeast(0f)
                    val bottomBound = (maxY - ballSizePx).coerceAtLeast(0f)

                    if (x <= 0f) {
                        x = 0f
                        vx = -vx * restitution
                    } else if (x >= rightBound) {
                        x = rightBound
                        vx = -vx * restitution
                    }

                    if (y <= 0f) {
                        y = 0f
                        vy = -vy * restitution
                    } else if (y >= bottomBound) {
                        y = bottomBound
                        vy = -vy * restitution
                        vx *= EasterEggConfig.FLOOR_FRICTION
                    }
                }
            }

            show = false
            onFinished()
        }

        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(animationResId)
                    .crossfade(true)
                    .build(),
                imageLoader = imageLoader,
                contentDescription = null,
                contentScale = ContentScale.Fit,
                onError = {
                    if (!forceFallback) {
                        forceFallback = true
                    }
                },
                modifier = Modifier
                    .offset { IntOffset(x.roundToInt(), y.roundToInt()) }
                    .size(ballSize)
                    .graphicsLayer {
                        alpha = animatedAlpha
                        rotationZ = rotation
                    }
            )
        }
    }
}



