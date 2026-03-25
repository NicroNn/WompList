package itmo.alk.womplist.domain.mylist.usecase

import itmo.alk.womplist.core.model.Anime
import itmo.alk.womplist.domain.anime.AnimeRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveWatchingListUseCase @Inject constructor(
    private val repository: AnimeRepository
) {
    operator fun invoke(): Flow<List<Anime>> {
        return repository.watchingList
    }
}

class ObservePlannedListUseCase @Inject constructor(
    private val repository: AnimeRepository
) {
    operator fun invoke(): Flow<List<Anime>> {
        return repository.plannedList
    }
}

class ObserveCompletedListUseCase @Inject constructor(
    private val repository: AnimeRepository
) {
    operator fun invoke(): Flow<List<Anime>> {
        return repository.completedList
    }
}

