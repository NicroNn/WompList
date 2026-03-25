package itmo.alk.womplist.feature.home

import itmo.alk.womplist.core.model.Anime
import itmo.alk.womplist.core.mvi.UiEffect
import itmo.alk.womplist.core.mvi.UiIntent
import itmo.alk.womplist.core.mvi.UiState

data class HomeState(
    val allAnime: List<Anime> = emptyList(),
    val searchQuery: String = "",
    val searchResults: List<Anime> = emptyList(),
    val isSearching: Boolean = false
) : UiState {
    val displayedList: List<Anime>
        get() = if (searchQuery.isBlank()) allAnime else searchResults
}

sealed interface HomeIntent : UiIntent {
    data class SearchChanged(val query: String) : HomeIntent
    data class OpenTitle(val id: Long) : HomeIntent
    data object TriggerSecret : HomeIntent
}

sealed interface HomeEffect : UiEffect {
    data class NavigateToTitle(val id: Long) : HomeEffect
    data object TriggerSecretOverlay : HomeEffect
}



