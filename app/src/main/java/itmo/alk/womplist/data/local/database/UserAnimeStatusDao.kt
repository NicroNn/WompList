package itmo.alk.womplist.data.local.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import itmo.alk.womplist.data.local.database.entity.UserAnimeStatusEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserAnimeStatusDao {
    @Query("SELECT * FROM user_anime_status WHERE status = :status")
    fun getAnimeIdsByStatus(status: String): Flow<List<UserAnimeStatusEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: UserAnimeStatusEntity)

    @Query("DELETE FROM user_anime_status WHERE animeId = :animeId")
    suspend fun delete(animeId: Long)

    @Query("SELECT status FROM user_anime_status WHERE animeId = :animeId")
    suspend fun getStatusForAnime(animeId: Long): String?

    @Query("SELECT status FROM user_anime_status WHERE animeId = :animeId")
    fun observeStatusForAnime(animeId: Long): Flow<String?>
}