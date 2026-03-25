package itmo.alk.womplist.testutil

import itmo.alk.womplist.core.model.Anime
import itmo.alk.womplist.data.repository.AnimeRepository
import itmo.alk.womplist.data.repository.AnimeStatus
import kotlinx.coroutines.flow.MutableStateFlow

class FakeAnimeRepository(
    initialAllAnime: List<Anime> = emptyList(),
    private val animeById: MutableMap<Long, Anime> = mutableMapOf(),
    private val statuses: MutableMap<Long, AnimeStatus> = mutableMapOf(),
    private val ratings: MutableMap<Long, Int> = mutableMapOf()
) : AnimeRepository {

    override val allAnime: MutableStateFlow<List<Anime>> = MutableStateFlow(initialAllAnime)
    override val watchingList: MutableStateFlow<List<Anime>> = MutableStateFlow(emptyList())
    override val plannedList: MutableStateFlow<List<Anime>> = MutableStateFlow(emptyList())
    override val completedList: MutableStateFlow<List<Anime>> = MutableStateFlow(emptyList())

    var addedToList: Pair<Long, AnimeStatus>? = null
    var removedFromList: Pair<Long, AnimeStatus>? = null
    var savedRating: Pair<Long, Int>? = null

    init {
        initialAllAnime.forEach { animeById[it.id] = it }
    }

    override suspend fun getAnimeById(id: Long): Anime? = animeById[id]

    override suspend fun addToList(anime: Anime, status: AnimeStatus) {
        animeById[anime.id] = anime
        statuses[anime.id] = status
        addedToList = anime.id to status
    }

    override suspend fun removeFromList(animeId: Long, status: AnimeStatus) {
        statuses.remove(animeId)
        removedFromList = animeId to status
    }

    override suspend fun getStatusForAnime(animeId: Long): AnimeStatus? = statuses[animeId]

    override suspend fun setUserRating(animeId: Long, rating: Int) {
        ratings[animeId] = rating
        savedRating = animeId to rating
    }

    override suspend fun getUserRatingForAnime(animeId: Long): Int? = ratings[animeId]

    override suspend fun searchAnime(query: String): List<Anime> {
        return allAnime.value.filter {
            it.name.contains(query, ignoreCase = true) ||
                (it.russianName?.contains(query, ignoreCase = true) == true)
        }
    }

    override suspend fun loadMoreAnime(page: Int) = Unit

    fun putAnime(anime: Anime) {
        animeById[anime.id] = anime
    }

    fun putStatus(animeId: Long, status: AnimeStatus) {
        statuses[animeId] = status
    }

    fun putRating(animeId: Long, rating: Int) {
        ratings[animeId] = rating
    }
}


