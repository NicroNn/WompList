package itmo.alk.womplist.feature.title

import itmo.alk.womplist.core.model.Anime
import itmo.alk.womplist.core.error.AppError
import itmo.alk.womplist.core.mvi.UiEffect
import itmo.alk.womplist.core.mvi.UiIntent
import itmo.alk.womplist.core.mvi.UiState
import itmo.alk.womplist.domain.anime.AnimeStatus

data class TitleState(
    val isLoading: Boolean = true,
    val anime: Anime? = null,
    val currentStatus: AnimeStatus? = null,
    val currentUserRating: Int? = null,
    val recommendations: List<Anime> = emptyList(),
    val isStatusDialogVisible: Boolean = false,
    val isRatingOverlayVisible: Boolean = false,
    val error: AppError? = null
) : UiState

sealed interface TitleIntent : UiIntent {
    data class Initialize(val titleId: Long) : TitleIntent
    data object ObserveRecommendations : TitleIntent
    data object Retry : TitleIntent
    data object OpenStatusDialog : TitleIntent
    data object DismissStatusDialog : TitleIntent
    data object OpenRatingOverlay : TitleIntent
    data object DismissRatingOverlay : TitleIntent
    data class SetStatus(val status: AnimeStatus) : TitleIntent
    data object RemoveFromList : TitleIntent
    data class SaveRating(val rating: Int) : TitleIntent
    data class OpenTitle(val id: Long) : TitleIntent
}

sealed interface TitleEffect : UiEffect {
    data class NavigateToTitle(val id: Long) : TitleEffect
}


