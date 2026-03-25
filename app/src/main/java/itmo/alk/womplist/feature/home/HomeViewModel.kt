package itmo.alk.womplist.feature.home

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import itmo.alk.womplist.core.error.toAppError
import itmo.alk.womplist.core.mvi.MviViewModel
import itmo.alk.womplist.domain.home.usecase.ObserveHomeAnimeUseCase
import itmo.alk.womplist.domain.home.usecase.SearchHomeAnimeUseCase
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val observeHomeAnime: ObserveHomeAnimeUseCase,
    private val searchHomeAnime: SearchHomeAnimeUseCase
) : MviViewModel<HomeState, HomeIntent, HomeEffect>(HomeState()) {

    private var observeJob: Job? = null
    private var searchJob: Job? = null

    init {
        observeAnime()
    }

    override suspend fun handleIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.SearchChanged -> onSearchChanged(intent.query)
            is HomeIntent.OpenTitle -> sendEffect(HomeEffect.NavigateToTitle(intent.id))
            HomeIntent.TriggerSecret -> sendEffect(HomeEffect.TriggerSecretOverlay)
            HomeIntent.Retry -> retry()
        }
    }

    private fun retry() {
        setState { it.copy(error = null) }
        observeJob?.cancel()
        observeJob = null
        observeAnime()

        val query = state.value.searchQuery
        if (query.isNotBlank()) {
            onSearchChanged(query)
        }
    }

    private fun observeAnime() {
        if (observeJob != null) return
        observeJob = viewModelScope.launch {
            observeHomeAnime()
                .catch { throwable ->
                    setState { it.copy(error = throwable.toAppError()) }
                }
                .collect { anime ->
                    setState { it.copy(allAnime = anime, error = null) }
                }
        }
    }

    private fun onSearchChanged(query: String) {
        setState { it.copy(searchQuery = query, error = null) }

        searchJob?.cancel()
        if (query.isBlank()) {
            setState { it.copy(searchResults = emptyList(), isSearching = false) }
            return
        }

        searchJob = viewModelScope.launch {
            try {
                setState { it.copy(isSearching = true, error = null) }
                val result = searchHomeAnime(query)
                setState { it.copy(searchResults = result, isSearching = false, error = null) }
            } catch (throwable: Throwable) {
                setState {
                    it.copy(
                        searchResults = emptyList(),
                        isSearching = false,
                        error = throwable.toAppError()
                    )
                }
            }
        }
    }
}



