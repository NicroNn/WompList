package itmo.alk.womplist.feature.settings

import itmo.alk.womplist.core.error.AppError
import itmo.alk.womplist.core.mvi.UiEffect
import itmo.alk.womplist.core.mvi.UiIntent
import itmo.alk.womplist.core.mvi.UiState

data class SettingsState(
    val darkTheme: Boolean = false,
    val language: String = "en",
    val error: AppError? = null
) : UiState

sealed interface SettingsIntent : UiIntent {
    data class ToggleDarkTheme(val enabled: Boolean) : SettingsIntent
    data class SetLanguage(val languageCode: String) : SettingsIntent
    data object Retry : SettingsIntent
}

sealed interface SettingsEffect : UiEffect


