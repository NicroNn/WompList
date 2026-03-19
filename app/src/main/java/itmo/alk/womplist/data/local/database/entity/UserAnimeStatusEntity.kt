package itmo.alk.womplist.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import itmo.alk.womplist.data.repository.AnimeStatus

@Entity(tableName = "user_anime_status")
data class UserAnimeStatusEntity(
    @PrimaryKey
    val animeId: Long,
    val status: AnimeStatus
)