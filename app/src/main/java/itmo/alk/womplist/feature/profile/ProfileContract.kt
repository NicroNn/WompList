package itmo.alk.womplist.feature.profile

import itmo.alk.womplist.core.model.Anime
import itmo.alk.womplist.core.error.AppError
import itmo.alk.womplist.core.mvi.UiEffect
import itmo.alk.womplist.core.mvi.UiIntent
import itmo.alk.womplist.core.mvi.UiState

data class ProfileState(
    val username: String = "Kostya Karenin",
    val watchingList: List<Anime> = emptyList(),
    val plannedList: List<Anime> = emptyList(),
    val completedList: List<Anime> = emptyList(),
    val error: AppError? = null
) : UiState {
    val continueWatching: List<Anime>
        get() = watchingList.take(2)
}

sealed interface ProfileIntent : UiIntent {
    data class OpenTitle(val id: Long) : ProfileIntent
    data object OpenSettings : ProfileIntent
    data object TriggerSecret : ProfileIntent
    data object Retry : ProfileIntent
}

sealed interface ProfileEffect : UiEffect {
    data class NavigateToTitle(val id: Long) : ProfileEffect
    data object NavigateToSettings : ProfileEffect
    data object TriggerSecretOverlay : ProfileEffect
}



