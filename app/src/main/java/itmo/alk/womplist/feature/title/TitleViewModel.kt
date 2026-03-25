package itmo.alk.womplist.feature.title

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import itmo.alk.womplist.core.error.toAppError
import itmo.alk.womplist.core.mvi.MviViewModel
import itmo.alk.womplist.domain.anime.AnimeStatus
import itmo.alk.womplist.domain.title.usecase.GetTitleByIdUseCase
import itmo.alk.womplist.domain.title.usecase.ObserveTitleRatingUseCase
import itmo.alk.womplist.domain.title.usecase.ObserveTitleRecommendationsUseCase
import itmo.alk.womplist.domain.title.usecase.ObserveTitleStatusUseCase
import itmo.alk.womplist.domain.title.usecase.RemoveTitleFromListUseCase
import itmo.alk.womplist.domain.title.usecase.SaveTitleRatingUseCase
import itmo.alk.womplist.domain.title.usecase.SetTitleStatusUseCase
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

@HiltViewModel
class TitleViewModel @Inject constructor(
    private val getTitleById: GetTitleByIdUseCase,
    private val observeRecommendationsUseCase: ObserveTitleRecommendationsUseCase,
    private val observeStatus: ObserveTitleStatusUseCase,
    private val observeRating: ObserveTitleRatingUseCase,
    private val setTitleStatus: SetTitleStatusUseCase,
    private val removeTitleFromList: RemoveTitleFromListUseCase,
    private val saveTitleRating: SaveTitleRatingUseCase
) : MviViewModel<TitleState, TitleIntent, TitleEffect>(TitleState()) {

    private var recommendationsJob: Job? = null
    private var statusJob: Job? = null
    private var ratingJob: Job? = null
    private var initializedTitleId: Long? = null

    override suspend fun handleIntent(intent: TitleIntent) {
        when (intent) {
            is TitleIntent.Initialize -> initialize(intent.titleId)
            TitleIntent.ObserveRecommendations -> observeRecommendations()
            TitleIntent.Retry -> retry()
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
        observeLocalUserData(titleId)
        observeRecommendations()
    }

    private suspend fun retry() {
        val titleId = initializedTitleId ?: return
        setState { it.copy(error = null) }
        recommendationsJob?.cancel()
        recommendationsJob = null
        statusJob?.cancel()
        statusJob = null
        ratingJob?.cancel()
        ratingJob = null
        loadTitle(titleId)
        observeLocalUserData(titleId)
        observeRecommendations()
    }

    private suspend fun loadTitle(titleId: Long) {
        try {
            setState { it.copy(isLoading = true, error = null) }
            val anime = getTitleById(titleId)
            setState {
                it.copy(
                    isLoading = false,
                    anime = anime,
                    error = null
                )
            }
        } catch (throwable: Throwable) {
            setState {
                it.copy(
                    isLoading = false,
                    anime = null,
                    error = throwable.toAppError()
                )
            }
        }
    }

    private fun observeLocalUserData(titleId: Long) {
        statusJob?.cancel()
        ratingJob?.cancel()

        statusJob = viewModelScope.launch {
            observeStatus(titleId)
                .catch { throwable ->
                    setState { it.copy(error = throwable.toAppError()) }
                }
                .collect { status ->
                    setState { it.copy(currentStatus = status) }
                }
        }

        ratingJob = viewModelScope.launch {
            observeRating(titleId)
                .catch { throwable ->
                    setState { it.copy(error = throwable.toAppError()) }
                }
                .collect { rating ->
                    setState { it.copy(currentUserRating = rating) }
                }
        }
    }

    private fun observeRecommendations() {
        if (recommendationsJob != null) return
        recommendationsJob = viewModelScope.launch {
            observeRecommendationsUseCase(limit = 3)
                .catch { throwable ->
                    setState { it.copy(error = throwable.toAppError()) }
                }
                .collect { list ->
                    setState { it.copy(recommendations = list) }
                }
        }
    }

    private suspend fun setStatus(status: AnimeStatus) {
        val anime = state.value.anime ?: return
        try {
            setTitleStatus(anime, status)
            setState { it.copy(currentStatus = status, isStatusDialogVisible = false, error = null) }
        } catch (throwable: Throwable) {
            setState { it.copy(error = throwable.toAppError()) }
        }
    }

    private suspend fun removeFromList() {
        val anime = state.value.anime ?: return
        val status = state.value.currentStatus ?: return
        try {
            removeTitleFromList(anime.id, status)
            setState { it.copy(currentStatus = null, isStatusDialogVisible = false, error = null) }
        } catch (throwable: Throwable) {
            setState { it.copy(error = throwable.toAppError()) }
        }
    }

    private suspend fun saveRating(rating: Int) {
        val anime = state.value.anime ?: return
        try {
            saveTitleRating(anime.id, rating)
            setState { it.copy(currentUserRating = rating, isRatingOverlayVisible = false, error = null) }
        } catch (throwable: Throwable) {
            setState { it.copy(error = throwable.toAppError()) }
        }
    }
}


