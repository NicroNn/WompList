package itmo.alk.womplist.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import itmo.alk.womplist.data.local.database.entity.UserAnimeRatingEntity
import itmo.alk.womplist.data.local.database.entity.UserAnimeStatusEntity
import itmo.alk.womplist.data.repository.AnimeStatus

class Converters {
    @TypeConverter
    fun fromAnimeStatus(status: AnimeStatus): String = status.name

    @TypeConverter
    fun toAnimeStatus(name: String): AnimeStatus = AnimeStatus.valueOf(name)
}

@Database(
    entities = [UserAnimeStatusEntity::class, UserAnimeRatingEntity::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userAnimeStatusDao(): UserAnimeStatusDao
    abstract fun userAnimeRatingDao(): UserAnimeRatingDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `user_anime_rating` (`animeId` INTEGER NOT NULL, `rating` INTEGER NOT NULL, PRIMARY KEY(`animeId`))"
                )
            }
        }

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "womplist_db"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}