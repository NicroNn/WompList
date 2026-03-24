package itmo.alk.womplist.core.ui.components

import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import itmo.alk.womplist.R

data class EasterEggAssetSpec(
    @param:RawRes val gifResId: Int? = null,
    @param:DrawableRes val fallbackDrawableResId: Int
)

object EasterEggConfig {
    const val HOLD_DURATION_MS = 3_000L
    const val BURST_WINDOW_MS = 1_200L
    const val TAPS_FOR_BURST = 6

    const val OVERLAY_TTL_MS = 12_000L
    const val GRAVITY = 2_100f
    const val RESTITUTION = 0.9f
    const val FLOOR_FRICTION = 0.99f

    // Основные анимации — GIF из res/raw, fallback — drawable на случай ошибки декодирования.
    fun assetFor(type: ScreenCornerType): EasterEggAssetSpec = when (type) {
        ScreenCornerType.HOME -> EasterEggAssetSpec(
            gifResId = R.raw.shylily_load,
            fallbackDrawableResId = R.drawable.womp
        )

        ScreenCornerType.MY_LIST -> EasterEggAssetSpec(
            gifResId = R.raw.shylily_love,
            fallbackDrawableResId = R.drawable.ic_launcher_foreground
        )

        ScreenCornerType.PROFILE -> EasterEggAssetSpec(
            gifResId = R.raw.shylily_wink,
            fallbackDrawableResId = R.drawable.ic_launcher_background
        )
    }
}




