package itmo.alk.womplist.data.local.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import itmo.alk.womplist.data.local.database.entity.UserAnimeRatingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserAnimeRatingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: UserAnimeRatingEntity)

    @Query("SELECT rating FROM user_anime_rating WHERE animeId = :animeId")
    suspend fun getRatingForAnime(animeId: Long): Int?

    @Query("SELECT rating FROM user_anime_rating WHERE animeId = :animeId")
    fun observeRatingForAnime(animeId: Long): Flow<Int?>

    @Query("DELETE FROM user_anime_rating WHERE animeId = :animeId")
    suspend fun delete(animeId: Long)
}

