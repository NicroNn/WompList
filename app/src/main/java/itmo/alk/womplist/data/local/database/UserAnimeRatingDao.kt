package itmo.alk.womplist.data.local.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import itmo.alk.womplist.data.local.database.entity.UserAnimeRatingEntity

@Dao
interface UserAnimeRatingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: UserAnimeRatingEntity)

    @Query("SELECT rating FROM user_anime_rating WHERE animeId = :animeId")
    suspend fun getRatingForAnime(animeId: Long): Int?

    @Query("DELETE FROM user_anime_rating WHERE animeId = :animeId")
    suspend fun delete(animeId: Long)
}

