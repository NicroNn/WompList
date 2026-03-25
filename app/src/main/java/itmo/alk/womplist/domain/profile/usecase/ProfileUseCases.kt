package itmo.alk.womplist.domain.profile.usecase

import itmo.alk.womplist.core.model.Anime
import itmo.alk.womplist.domain.anime.AnimeRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveProfileWatchingListUseCase @Inject constructor(
    private val repository: AnimeRepository
) {
    operator fun invoke(): Flow<List<Anime>> {
        return repository.watchingList
    }
}

class ObserveProfilePlannedListUseCase @Inject constructor(
    private val repository: AnimeRepository
) {
    operator fun invoke(): Flow<List<Anime>> {
        return repository.plannedList
    }
}

class ObserveProfileCompletedListUseCase @Inject constructor(
    private val repository: AnimeRepository
) {
    operator fun invoke(): Flow<List<Anime>> {
        return repository.completedList
    }
}

