package itmo.alk.womplist.domain.home.usecase

import itmo.alk.womplist.core.model.Anime
import itmo.alk.womplist.domain.anime.AnimeRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveHomeAnimeUseCase @Inject constructor(
    private val repository: AnimeRepository
) {
    operator fun invoke(): Flow<List<Anime>> {
        return repository.allAnime
    }
}

class SearchHomeAnimeUseCase @Inject constructor(
    private val repository: AnimeRepository
) {
    suspend operator fun invoke(query: String): List<Anime> {
        return repository.searchAnime(query)
    }
}

