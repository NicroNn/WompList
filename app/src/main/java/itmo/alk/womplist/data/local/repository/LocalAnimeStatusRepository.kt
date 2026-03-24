package itmo.alk.womplist.data.local.repository

import itmo.alk.womplist.data.local.database.AppDatabase
import itmo.alk.womplist.data.local.database.entity.UserAnimeRatingEntity
import itmo.alk.womplist.data.local.database.entity.UserAnimeStatusEntity
import itmo.alk.womplist.data.repository.AnimeStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocalAnimeStatusRepository(
    private val db: AppDatabase
) {
    private val dao = db.userAnimeStatusDao()
    private val ratingDao = db.userAnimeRatingDao()

    fun getWatchingIds(): Flow<List<Long>> = dao.getAnimeIdsByStatus(AnimeStatus.WATCHING.name)
        .map { list -> list.map { it.animeId } }

    fun getPlannedIds(): Flow<List<Long>> = dao.getAnimeIdsByStatus(AnimeStatus.PLANNED.name)
        .map { list -> list.map { it.animeId } }

    fun getCompletedIds(): Flow<List<Long>> = dao.getAnimeIdsByStatus(AnimeStatus.COMPLETED.name)
        .map { list -> list.map { it.animeId } }

    suspend fun setStatus(animeId: Long, status: AnimeStatus) {
        dao.insert(UserAnimeStatusEntity(animeId, status))
    }

    suspend fun removeStatus(animeId: Long) {
        dao.delete(animeId)
    }

    suspend fun getStatus(animeId: Long): AnimeStatus? {
        val name = dao.getStatusForAnime(animeId) ?: return null
        return AnimeStatus.valueOf(name)
    }

    suspend fun setRating(animeId: Long, rating: Int) {
        ratingDao.upsert(UserAnimeRatingEntity(animeId = animeId, rating = rating))
    }

    suspend fun getRating(animeId: Long): Int? {
        return ratingDao.getRatingForAnime(animeId)
    }

    suspend fun removeRating(animeId: Long) {
        ratingDao.delete(animeId)
    }
}