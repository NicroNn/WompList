package itmo.alk.womplist.feature.mylist

import itmo.alk.womplist.core.model.Anime
import itmo.alk.womplist.core.error.AppError
import itmo.alk.womplist.core.mvi.UiEffect
import itmo.alk.womplist.core.mvi.UiIntent
import itmo.alk.womplist.core.mvi.UiState

enum class MyListFilter {
    WATCHING,
    PLANNED,
    COMPLETED
}

data class MyListState(
    val watchingList: List<Anime> = emptyList(),
    val plannedList: List<Anime> = emptyList(),
    val completedList: List<Anime> = emptyList(),
    val selectedFilter: MyListFilter = MyListFilter.WATCHING,
    val isStatusMenuExpanded: Boolean = false,
    val error: AppError? = null
) : UiState {
    val selectedList: List<Anime>
        get() = when (selectedFilter) {
            MyListFilter.WATCHING -> watchingList
            MyListFilter.PLANNED -> plannedList
            MyListFilter.COMPLETED -> completedList
        }
}

sealed interface MyListIntent : UiIntent {
    data class SelectFilter(val filter: MyListFilter) : MyListIntent
    data class ToggleStatusMenu(val expanded: Boolean) : MyListIntent
    data class OpenTitle(val id: Long) : MyListIntent
    data object TriggerSecret : MyListIntent
    data object Retry : MyListIntent
}

sealed interface MyListEffect : UiEffect {
    data class NavigateToTitle(val id: Long) : MyListEffect
    data object TriggerSecretOverlay : MyListEffect
}



