package itmo.alk.womplist.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import itmo.alk.womplist.data.local.database.entity.UserAnimeStatusEntity
import itmo.alk.womplist.data.repository.AnimeStatus

class Converters {
    @TypeConverter
    fun fromAnimeStatus(status: AnimeStatus): String = status.name

    @TypeConverter
    fun toAnimeStatus(name: String): AnimeStatus = AnimeStatus.valueOf(name)
}

@Database(
    entities = [UserAnimeStatusEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userAnimeStatusDao(): UserAnimeStatusDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "womplist_db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}