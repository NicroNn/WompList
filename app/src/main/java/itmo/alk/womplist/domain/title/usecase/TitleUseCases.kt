package itmo.alk.womplist.domain.title.usecase

import itmo.alk.womplist.core.model.Anime
import itmo.alk.womplist.domain.anime.AnimeRepository
import itmo.alk.womplist.domain.anime.AnimeStatus
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetTitleByIdUseCase @Inject constructor(
    private val repository: AnimeRepository
) {
    suspend operator fun invoke(titleId: Long): Anime? {
        return repository.getAnimeById(titleId)
    }
}

class ObserveTitleRecommendationsUseCase @Inject constructor(
    private val repository: AnimeRepository
) {
    operator fun invoke(limit: Int = 3): Flow<List<Anime>> {
        return repository.allAnime.map { anime -> anime.take(limit) }
    }
}

class ObserveTitleStatusUseCase @Inject constructor(
    private val repository: AnimeRepository
) {
    operator fun invoke(titleId: Long): Flow<AnimeStatus?> {
        return repository.observeStatusForAnime(titleId)
    }
}

class ObserveTitleRatingUseCase @Inject constructor(
    private val repository: AnimeRepository
) {
    operator fun invoke(titleId: Long): Flow<Int?> {
        return repository.observeUserRatingForAnime(titleId)
    }
}

class SetTitleStatusUseCase @Inject constructor(
    private val repository: AnimeRepository
) {
    suspend operator fun invoke(anime: Anime, status: AnimeStatus) {
        repository.addToList(anime, status)
    }
}

class RemoveTitleFromListUseCase @Inject constructor(
    private val repository: AnimeRepository
) {
    suspend operator fun invoke(animeId: Long, status: AnimeStatus) {
        repository.removeFromList(animeId, status)
    }
}

class SaveTitleRatingUseCase @Inject constructor(
    private val repository: AnimeRepository
) {
    suspend operator fun invoke(animeId: Long, rating: Int) {
        repository.setUserRating(animeId, rating)
    }
}

