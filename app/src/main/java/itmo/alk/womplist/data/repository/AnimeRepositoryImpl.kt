package itmo.alk.womplist.data.repository

import itmo.alk.womplist.core.model.Anime
import itmo.alk.womplist.data.local.repository.LocalAnimeStatusRepository
import itmo.alk.womplist.data.network.repository.NetworkAnimeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class AnimeRepositoryImpl(
    private val networkRepo: NetworkAnimeRepository,
    private val localStatusRepo: LocalAnimeStatusRepository
) : AnimeRepository {

    override val allAnime: Flow<List<Anime>> = networkRepo.animeCache.map { it.values.toList() }

    override val watchingList: Flow<List<Anime>> = combine(
        localStatusRepo.getWatchingIds(),
        networkRepo.animeCache
    ) { ids, cache ->
        ids.mapNotNull { cache[it] }
    }

    override val plannedList: Flow<List<Anime>> = combine(
        localStatusRepo.getPlannedIds(),
        networkRepo.animeCache
    ) { ids, cache ->
        ids.mapNotNull { cache[it] }
    }

    override val completedList: Flow<List<Anime>> = combine(
        localStatusRepo.getCompletedIds(),
        networkRepo.animeCache
    ) { ids, cache ->
        ids.mapNotNull { cache[it] }
    }

    override suspend fun getAnimeById(id: Long): Anime? {
        return networkRepo.getAnimeById(id)
    }

    override suspend fun addToList(anime: Anime, status: AnimeStatus) {
        localStatusRepo.setStatus(anime.id, status)
    }

    override suspend fun removeFromList(animeId: Long, status: AnimeStatus) {
        localStatusRepo.removeStatus(animeId)
    }

    override suspend fun getStatusForAnime(animeId: Long): AnimeStatus? {
        return localStatusRepo.getStatus(animeId)
    }

    override suspend fun searchAnime(query: String): List<Anime> {
        return networkRepo.searchAnime(query)
    }

    override suspend fun loadMoreAnime(page: Int) {
        networkRepo.loadAnimeList(page = page)
    }
}