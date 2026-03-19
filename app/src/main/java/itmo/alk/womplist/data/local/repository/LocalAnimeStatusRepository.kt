package itmo.alk.womplist.data.local.repository

import itmo.alk.womplist.data.local.database.AppDatabase
import itmo.alk.womplist.data.local.database.entity.UserAnimeStatusEntity
import itmo.alk.womplist.data.repository.AnimeStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocalAnimeStatusRepository(
    private val db: AppDatabase
) {
    private val dao = db.userAnimeStatusDao()

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
}