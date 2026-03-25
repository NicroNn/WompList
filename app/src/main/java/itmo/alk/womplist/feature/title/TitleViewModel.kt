package itmo.alk.womplist.feature.title

import androidx.lifecycle.viewModelScope
import itmo.alk.womplist.core.mvi.MviViewModel
import itmo.alk.womplist.data.repository.AnimeRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class TitleViewModel(
    private val repository: AnimeRepository
) : MviViewModel<TitleState, TitleIntent, TitleEffect>(TitleState()) {

    private var recommendationsJob: Job? = null
    private var initializedTitleId: Long? = null

    override suspend fun handleIntent(intent: TitleIntent) {
        when (intent) {
            is TitleIntent.Initialize -> initialize(intent.titleId)
            TitleIntent.ObserveRecommendations -> observeRecommendations()
            TitleIntent.OpenStatusDialog -> setState { it.copy(isStatusDialogVisible = true) }
            TitleIntent.DismissStatusDialog -> setState { it.copy(isStatusDialogVisible = false) }
            TitleIntent.OpenRatingOverlay -> setState { it.copy(isRatingOverlayVisible = true) }
            TitleIntent.DismissRatingOverlay -> setState { it.copy(isRatingOverlayVisible = false) }
            is TitleIntent.SetStatus -> setStatus(intent.status)
            TitleIntent.RemoveFromList -> removeFromList()
            is TitleIntent.SaveRating -> saveRating(intent.rating)
            is TitleIntent.OpenTitle -> sendEffect(TitleEffect.NavigateToTitle(intent.id))
        }
    }

    private suspend fun initialize(titleId: Long) {
        if (initializedTitleId == titleId && state.value.anime != null) return
        initializedTitleId = titleId
        loadTitle(titleId)
        observeRecommendations()
    }

    private suspend fun loadTitle(titleId: Long) {
        setState { it.copy(isLoading = true) }
        val anime = repository.getAnimeById(titleId)
        val status = anime?.let { repository.getStatusForAnime(it.id) }
        val rating = anime?.let { repository.getUserRatingForAnime(it.id) }
        setState {
            it.copy(
                isLoading = false,
                anime = anime,
                currentStatus = status,
                currentUserRating = rating
            )
        }
    }

    private fun observeRecommendations() {
        if (recommendationsJob != null) return
        recommendationsJob = viewModelScope.launch {
            repository.allAnime.collect { list ->
                setState { it.copy(recommendations = list.take(3)) }
            }
        }
    }

    private suspend fun setStatus(status: itmo.alk.womplist.data.repository.AnimeStatus) {
        val anime = state.value.anime ?: return
        repository.addToList(anime, status)
        setState { it.copy(currentStatus = status, isStatusDialogVisible = false) }
    }

    private suspend fun removeFromList() {
        val anime = state.value.anime ?: return
        val status = state.value.currentStatus ?: return
        repository.removeFromList(anime.id, status)
        setState { it.copy(currentStatus = null, isStatusDialogVisible = false) }
    }

    private suspend fun saveRating(rating: Int) {
        val anime = state.value.anime ?: return
        repository.setUserRating(anime.id, rating)
        setState { it.copy(currentUserRating = rating, isRatingOverlayVisible = false) }
    }
}


