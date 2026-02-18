package itmo.alk.womplist.data.repository

import itmo.alk.womplist.core.model.Anime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class AnimeStatus {
    WATCHING, PLANNED, COMPLETED
}

class AnimeRepository {
    private val _allAnime = MutableStateFlow(
        listOf(
            Anime(1, "Attack on Titan",
                description = "Several hundred years ago, humans were nearly exterminated by giants...",
                episodes = 75,
                genres = listOf("Action", "Drama", "Thriller"),
                episodesList = (1..75).map { "Episode $it" },
                rating = 8.5,
                year = 2013
            ),
            Anime(2, "My Hero Academia",
                description = "In a world where people with superpowers known as 'Quirks' are the norm...",
                episodes = 113,
                genres = listOf("Action", "Comedy", "Superhero"),
                episodesList = (1..113).map { "Episode $it" },
                rating = 8.3,
                year = 2016
            ),
            Anime(3, "One Piece", genres = listOf("Adventure", "Comedy")),
            Anime(4, "Naruto", genres = listOf("Action", "Adventure")),
            Anime(5, "Demon Slayer", genres = listOf("Action", "Fantasy")),
            Anime(6, "Jujutsu Kaisen", genres = listOf("Action", "Supernatural"))
        )
    )
    val allAnime: StateFlow<List<Anime>> = _allAnime.asStateFlow()

    private val _watchingList = MutableStateFlow<List<Anime>>(emptyList())
    val watchingList: StateFlow<List<Anime>> = _watchingList.asStateFlow()

    private val _plannedList = MutableStateFlow<List<Anime>>(emptyList())
    val plannedList: StateFlow<List<Anime>> = _plannedList.asStateFlow()

    private val _completedList = MutableStateFlow<List<Anime>>(emptyList())
    val completedList: StateFlow<List<Anime>> = _completedList.asStateFlow()

    init {}

    fun getAnimeById(id: Long): Anime? {
        return _allAnime.value.find { it.id == id }
    }

    fun addToList(anime: Anime, status: AnimeStatus) {
        when (status) {
            AnimeStatus.WATCHING -> {
                if (_watchingList.value.none { it.id == anime.id }) {
                    _watchingList.update { it + anime }
                }
            }
            AnimeStatus.PLANNED -> {
                if (_plannedList.value.none { it.id == anime.id }) {
                    _plannedList.update { it + anime }
                }
            }
            AnimeStatus.COMPLETED -> {
                if (_completedList.value.none { it.id == anime.id }) {
                    _completedList.update { it + anime }
                }
            }
        }
    }

    fun removeFromList(animeId: Long, status: AnimeStatus) {
        when (status) {
            AnimeStatus.WATCHING -> _watchingList.update { it.filter { it.id != animeId } }
            AnimeStatus.PLANNED -> _plannedList.update { it.filter { it.id != animeId } }
            AnimeStatus.COMPLETED -> _completedList.update { it.filter { it.id != animeId } }
        }
    }

    fun getStatusForAnime(animeId: Long): AnimeStatus? {
        return when {
            _watchingList.value.any { it.id == animeId } -> AnimeStatus.WATCHING
            _plannedList.value.any { it.id == animeId } -> AnimeStatus.PLANNED
            _completedList.value.any { it.id == animeId } -> AnimeStatus.COMPLETED
            else -> null
        }
    }

    fun searchAnime(query: String): List<Anime> {
        if (query.isBlank()) return _allAnime.value
        return _allAnime.value.filter {
            it.title.contains(query, ignoreCase = true)
        }
    }
}