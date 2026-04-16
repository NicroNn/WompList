package itmo.alk.womplist.feature.home

import itmo.alk.womplist.core.model.Anime
import itmo.alk.womplist.core.sdui.ScrollDirection

data class HomeState(
    val searchQuery: String,
    val displayedList: List<Anime>,
    val isSearching: Boolean,
    val onSearchChange: (String) -> Unit,
    val onSecretTrigger: () -> Unit,
    val onScrollDirectionChange: (ScrollDirection) -> Unit
)