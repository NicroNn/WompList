package itmo.alk.womplist.feature.home

import androidx.lifecycle.viewModelScope
import itmo.alk.womplist.core.mvi.MviViewModel
import itmo.alk.womplist.data.repository.AnimeRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: AnimeRepository
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
        }
    }

    private fun observeAnime() {
        if (observeJob != null) return
        observeJob = viewModelScope.launch {
            repository.allAnime.collect { anime ->
                setState { it.copy(allAnime = anime) }
            }
        }
    }

    private fun onSearchChanged(query: String) {
        setState { it.copy(searchQuery = query) }

        searchJob?.cancel()
        if (query.isBlank()) {
            setState { it.copy(searchResults = emptyList(), isSearching = false) }
            return
        }

        searchJob = viewModelScope.launch {
            setState { it.copy(isSearching = true) }
            val result = repository.searchAnime(query)
            setState { it.copy(searchResults = result, isSearching = false) }
        }
    }
}



