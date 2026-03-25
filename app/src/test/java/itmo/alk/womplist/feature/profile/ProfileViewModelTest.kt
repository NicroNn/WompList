package itmo.alk.womplist.feature.profile

import app.cash.turbine.test
import itmo.alk.womplist.core.error.AppError
import itmo.alk.womplist.core.model.Anime
import itmo.alk.womplist.domain.profile.usecase.ObserveProfileCompletedListUseCase
import itmo.alk.womplist.domain.profile.usecase.ObserveProfilePlannedListUseCase
import itmo.alk.womplist.domain.profile.usecase.ObserveProfileWatchingListUseCase
import itmo.alk.womplist.testutil.FakeAnimeRepository
import itmo.alk.womplist.testutil.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `init observes profile lists`() = runTest {
        val watching = listOf(testAnime(1, "Naruto"), testAnime(2, "Bleach"))
        val planned = listOf(testAnime(3, "JJK"))
        val completed = listOf(testAnime(4, "AOT"))
        val repository = FakeAnimeRepository().apply {
            setWatchingList(watching)
            setPlannedList(planned)
            setCompletedList(completed)
        }

        val viewModel = createViewModel(repository)
        advanceUntilIdle()

        with(viewModel.state.value) {
            assertEquals(watching, watchingList)
            assertEquals(planned, plannedList)
            assertEquals(completed, completedList)
            assertEquals(2, continueWatching.size)
        }
    }

    @Test
    fun `open settings sends navigate settings effect`() = runTest {
        val viewModel = createViewModel(FakeAnimeRepository())

        viewModel.effects.test {
            viewModel.onIntent(ProfileIntent.OpenSettings)
            assertEquals(ProfileEffect.NavigateToSettings, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `open title sends navigate title effect`() = runTest {
        val viewModel = createViewModel(FakeAnimeRepository())

        viewModel.effects.test {
            viewModel.onIntent(ProfileIntent.OpenTitle(55L))
            assertEquals(ProfileEffect.NavigateToTitle(55L), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `trigger secret sends secret overlay effect`() = runTest {
        val viewModel = createViewModel(FakeAnimeRepository())

        viewModel.effects.test {
            viewModel.onIntent(ProfileIntent.TriggerSecret)
            assertEquals(ProfileEffect.TriggerSecretOverlay, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `planned observe error updates error state`() = runTest {
        val repository = FakeAnimeRepository().apply {
            throwOnObservePlannedList = IllegalStateException("planned load failed")
        }
        val viewModel = createViewModel(repository)

        advanceUntilIdle()

        assertTrue(viewModel.state.value.error is AppError.Unknown)
    }

    private fun testAnime(id: Long, name: String): Anime {
        return Anime(
            id = id,
            name = name,
            russianName = name,
            posterUrl = "https://example.com/$id.jpg",
            descriptionHtml = "<p>desc</p>",
            episodes = 12,
            episodesAired = 12,
            status = "released",
            genres = listOf("Action"),
            score = 8.0,
            year = 2020
        )
    }

    private fun createViewModel(repository: FakeAnimeRepository): ProfileViewModel {
        return ProfileViewModel(
            observeWatchingList = ObserveProfileWatchingListUseCase(repository),
            observePlannedList = ObserveProfilePlannedListUseCase(repository),
            observeCompletedList = ObserveProfileCompletedListUseCase(repository)
        )
    }
}
