package itmo.alk.womplist.feature.home

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import itmo.alk.womplist.core.model.Anime
import itmo.alk.womplist.core.sdui.ScrollDirection

data class HomeState(
    val searchQuery: String,
    val displayedList: List<Anime>,
    val isSearching: Boolean,
    val listState: LazyListState,
    val gridState: LazyGridState,
    val onSearchChange: (String) -> Unit,
    val onSecretTrigger: () -> Unit,
    val onScrollDirectionChange: (ScrollDirection) -> Unit
)