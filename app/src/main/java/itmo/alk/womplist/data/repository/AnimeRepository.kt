package itmo.alk.womplist.data.repository

import itmo.alk.womplist.core.model.Anime
import kotlinx.coroutines.flow.Flow

interface AnimeRepository {
    val allAnime: Flow<List<Anime>>
    val watchingList: Flow<List<Anime>>
    val plannedList: Flow<List<Anime>>
    val completedList: Flow<List<Anime>>

    suspend fun getAnimeById(id: Long): Anime?
    suspend fun addToList(anime: Anime, status: AnimeStatus)
    suspend fun removeFromList(animeId: Long, status: AnimeStatus)
    suspend fun getStatusForAnime(animeId: Long): AnimeStatus?
    suspend fun searchAnime(query: String): List<Anime>
    suspend fun loadMoreAnime(page: Int)
}