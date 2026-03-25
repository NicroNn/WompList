package itmo.alk.womplist.di

import android.content.Context
import com.apollographql.apollo.ApolloClient
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import itmo.alk.womplist.data.local.database.AppDatabase
import itmo.alk.womplist.data.network.apollo.ApolloClientProvider
import itmo.alk.womplist.data.repository.AnimeRepositoryImpl
import itmo.alk.womplist.data.repository.UserPreferencesGatewayAdapter
import itmo.alk.womplist.domain.anime.AnimeRepository
import itmo.alk.womplist.domain.settings.SettingsPreferencesGateway
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CoreDataModule {

    @Provides
    @Singleton
    fun provideApolloClient(): ApolloClient {
        return ApolloClientProvider.getClient(accessToken = null)
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAnimeRepository(impl: AnimeRepositoryImpl): AnimeRepository

    @Binds
    @Singleton
    abstract fun bindSettingsPreferencesGateway(
        impl: UserPreferencesGatewayAdapter
    ): SettingsPreferencesGateway
}


