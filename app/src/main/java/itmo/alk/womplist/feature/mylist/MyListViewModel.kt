package itmo.alk.womplist.feature.mylist

import androidx.lifecycle.viewModelScope
import itmo.alk.womplist.core.mvi.MviViewModel
import itmo.alk.womplist.data.repository.AnimeRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MyListViewModel(
    private val repository: AnimeRepository
) : MviViewModel<MyListState, MyListIntent, MyListEffect>(MyListState()) {

    private var observeJob: Job? = null

    init {
        observeLists()
    }

    override suspend fun handleIntent(intent: MyListIntent) {
        when (intent) {
            is MyListIntent.SelectFilter -> {
                setState { it.copy(selectedFilter = intent.filter, isStatusMenuExpanded = false) }
            }

            is MyListIntent.ToggleStatusMenu -> setState { it.copy(isStatusMenuExpanded = intent.expanded) }
            is MyListIntent.OpenTitle -> sendEffect(MyListEffect.NavigateToTitle(intent.id))
            MyListIntent.TriggerSecret -> sendEffect(MyListEffect.TriggerSecretOverlay)
        }
    }

    private fun observeLists() {
        if (observeJob != null) return
        observeJob = viewModelScope.launch {
            launch {
                repository.watchingList.collect { list ->
                    setState { it.copy(watchingList = list) }
                }
            }
            launch {
                repository.plannedList.collect { list ->
                    setState { it.copy(plannedList = list) }
                }
            }
            launch {
                repository.completedList.collect { list ->
                    setState { it.copy(completedList = list) }
                }
            }
        }
    }
}



