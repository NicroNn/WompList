package itmo.alk.womplist.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_anime_rating")
data class UserAnimeRatingEntity(
    @PrimaryKey
    val animeId: Long,
    val rating: Int
)

