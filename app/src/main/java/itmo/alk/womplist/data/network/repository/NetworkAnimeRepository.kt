package itmo.alk.womplist.data.network.repository

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import itmo.alk.womplist.core.model.Anime
import itmo.alk.womplist.graphql.GetAnimeByIdQuery
import itmo.alk.womplist.graphql.GetAnimeListQuery
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class NetworkAnimeRepository(
    private val apolloClient: ApolloClient
) {
    private val _animeCache = MutableStateFlow<Map<Long, Anime>>(emptyMap())
    val animeCache: StateFlow<Map<Long, Anime>> = _animeCache.asStateFlow()

    suspend fun loadAnimeList(
        page: Int = 1,
        limit: Int = 50,
        search: String? = null
    ): List<Anime> {
        val response = apolloClient.query(
            GetAnimeListQuery(
                page = Optional.present(page),
                limit = Optional.present(limit),
                search = Optional.presentIfNotNull(search)
            )
        ).execute()

        val animeList = response.data?.animes?.mapNotNull { it?.toAnime() } ?: emptyList()

        val updatedMap = _animeCache.value.toMutableMap()
        animeList.forEach { updatedMap[it.id] = it }
        _animeCache.value = updatedMap

        return animeList
    }

    suspend fun getAnimeById(id: Long): Anime? {
        _animeCache.value[id]?.let { return it }

        val response = apolloClient.query(
            GetAnimeByIdQuery(id.toString())
        ).execute()

        val media = response.data?.animes?.firstOrNull() ?: return null
        val anime = media.toAnime()

        _animeCache.update { it + (anime.id to anime) }
        return anime
    }

    suspend fun searchAnime(query: String): List<Anime> {
        return loadAnimeList(page = 1, limit = 20, search = query)
    }

    private fun GetAnimeListQuery.Anime.toAnime(): Anime {
        val year = this.airedOn?.year
        val genreNames = this.genres?.mapNotNull { it?.russian ?: it?.name } ?: emptyList()

        return Anime(
            id = this.id.toLong(),
            name = this.name,
            russianName = this.russian,
            posterUrl = this.poster?.mainUrl ?: "",
            descriptionHtml = this.descriptionHtml ?: "",
            episodes = this.episodes,
            episodesAired = this.episodesAired,
            status = this.status?.name ?: "",
            genres = genreNames,
            score = this.score ?: 0.0,
            year = year
        )
    }

    private fun GetAnimeByIdQuery.Anime.toAnime(): Anime {
        val year = this.airedOn?.year
        val genreNames = this.genres?.mapNotNull { it?.russian ?: it?.name } ?: emptyList()

        return Anime(
            id = this.id.toLong(),
            name = this.name,
            russianName = this.russian,
            posterUrl = this.poster?.mainUrl ?: "",
            descriptionHtml = this.descriptionHtml ?: "",
            episodes = this.episodes,
            episodesAired = this.episodesAired,
            status = this.status?.name ?: "",
            genres = genreNames,
            score = this.score ?: 0.0,
            year = year
        )
    }
}