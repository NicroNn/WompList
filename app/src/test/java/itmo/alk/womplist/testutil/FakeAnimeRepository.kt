package itmo.alk.womplist.testutil

import itmo.alk.womplist.core.model.Anime
import itmo.alk.womplist.data.repository.AnimeRepository
import itmo.alk.womplist.data.repository.AnimeStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class FakeAnimeRepository(
    initialAllAnime: List<Anime> = emptyList(),
    private val animeById: MutableMap<Long, Anime> = mutableMapOf(),
    private val statuses: MutableMap<Long, AnimeStatus> = mutableMapOf(),
    private val ratings: MutableMap<Long, Int> = mutableMapOf()
) : AnimeRepository {

    override val allAnime: MutableStateFlow<List<Anime>> = MutableStateFlow(initialAllAnime)
    private val watchingState = MutableStateFlow<List<Anime>>(emptyList())
    private val plannedState = MutableStateFlow<List<Anime>>(emptyList())
    private val completedState = MutableStateFlow<List<Anime>>(emptyList())
    override val watchingList: Flow<List<Anime>>
        get() = throwOnObserveWatchingList?.let { throwable -> flow { throw throwable } } ?: watchingState
    override val plannedList: Flow<List<Anime>>
        get() = throwOnObservePlannedList?.let { throwable -> flow { throw throwable } } ?: plannedState
    override val completedList: Flow<List<Anime>>
        get() = throwOnObserveCompletedList?.let { throwable -> flow { throw throwable } } ?: completedState
    private val statusesFlow = MutableStateFlow(statuses.toMap())
    private val ratingsFlow = MutableStateFlow(ratings.toMap())

    var addedToList: Pair<Long, AnimeStatus>? = null
    var removedFromList: Pair<Long, AnimeStatus>? = null
    var savedRating: Pair<Long, Int>? = null
    var throwOnGetAnimeById: Throwable? = null
    var throwOnSearch: Throwable? = null
    var throwOnAddToList: Throwable? = null
    var throwOnRemoveFromList: Throwable? = null
    var throwOnSetRating: Throwable? = null
    var throwOnObserveWatchingList: Throwable? = null
    var throwOnObservePlannedList: Throwable? = null
    var throwOnObserveCompletedList: Throwable? = null

    init {
        initialAllAnime.forEach { animeById[it.id] = it }
    }

    override suspend fun getAnimeById(id: Long): Anime? {
        throwOnGetAnimeById?.let { throw it }
        return animeById[id]
    }

    override suspend fun addToList(anime: Anime, status: AnimeStatus) {
        throwOnAddToList?.let { throw it }
        animeById[anime.id] = anime
        statuses[anime.id] = status
        statusesFlow.value = statuses.toMap()
        addedToList = anime.id to status
    }

    override suspend fun removeFromList(animeId: Long, status: AnimeStatus) {
        throwOnRemoveFromList?.let { throw it }
        statuses.remove(animeId)
        statusesFlow.value = statuses.toMap()
        removedFromList = animeId to status
    }

    override suspend fun getStatusForAnime(animeId: Long): AnimeStatus? = statuses[animeId]

    override fun observeStatusForAnime(animeId: Long) = statusesFlow.map { it[animeId] }

    override suspend fun setUserRating(animeId: Long, rating: Int) {
        throwOnSetRating?.let { throw it }
        ratings[animeId] = rating
        ratingsFlow.value = ratings.toMap()
        savedRating = animeId to rating
    }

    override suspend fun getUserRatingForAnime(animeId: Long): Int? = ratings[animeId]

    override fun observeUserRatingForAnime(animeId: Long) = ratingsFlow.map { it[animeId] }

    override suspend fun searchAnime(query: String): List<Anime> {
        throwOnSearch?.let { throw it }
        return allAnime.value.filter {
            it.name.contains(query, ignoreCase = true) ||
                (it.russianName?.contains(query, ignoreCase = true) == true)
        }
    }

    override suspend fun loadMoreAnime(page: Int) = Unit

    fun putAnime(anime: Anime) {
        animeById[anime.id] = anime
    }

    fun setWatchingList(list: List<Anime>) {
        watchingState.value = list
    }

    fun setPlannedList(list: List<Anime>) {
        plannedState.value = list
    }

    fun setCompletedList(list: List<Anime>) {
        completedState.value = list
    }

    fun putStatus(animeId: Long, status: AnimeStatus) {
        statuses[animeId] = status
        statusesFlow.value = statuses.toMap()
    }

    fun putRating(animeId: Long, rating: Int) {
        ratings[animeId] = rating
        ratingsFlow.value = ratings.toMap()
    }
}


