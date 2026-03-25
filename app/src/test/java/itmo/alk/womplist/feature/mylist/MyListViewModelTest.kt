package itmo.alk.womplist.feature.mylist

import app.cash.turbine.test
import itmo.alk.womplist.core.error.AppError
import itmo.alk.womplist.core.model.Anime
import itmo.alk.womplist.domain.mylist.usecase.ObserveCompletedListUseCase
import itmo.alk.womplist.domain.mylist.usecase.ObservePlannedListUseCase
import itmo.alk.womplist.domain.mylist.usecase.ObserveWatchingListUseCase
import itmo.alk.womplist.testutil.FakeAnimeRepository
import itmo.alk.womplist.testutil.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MyListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `init observes all user lists`() = runTest {
        val watching = listOf(testAnime(1, "Naruto"))
        val planned = listOf(testAnime(2, "Bleach"))
        val completed = listOf(testAnime(3, "One Piece"))
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
            assertEquals(MyListFilter.WATCHING, selectedFilter)
        }
    }

    @Test
    fun `select filter updates state and closes menu`() = runTest {
        val viewModel = createViewModel(FakeAnimeRepository())

        viewModel.onIntent(MyListIntent.ToggleStatusMenu(true))
        viewModel.onIntent(MyListIntent.SelectFilter(MyListFilter.COMPLETED))
        advanceUntilIdle()

        assertEquals(MyListFilter.COMPLETED, viewModel.state.value.selectedFilter)
        assertFalse(viewModel.state.value.isStatusMenuExpanded)
    }

    @Test
    fun `open title sends navigation effect`() = runTest {
        val viewModel = createViewModel(FakeAnimeRepository())

        viewModel.effects.test {
            viewModel.onIntent(MyListIntent.OpenTitle(101L))
            assertEquals(MyListEffect.NavigateToTitle(101L), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `trigger secret sends secret overlay effect`() = runTest {
        val viewModel = createViewModel(FakeAnimeRepository())

        viewModel.effects.test {
            viewModel.onIntent(MyListIntent.TriggerSecret)
            assertEquals(MyListEffect.TriggerSecretOverlay, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `watching observe error updates error state`() = runTest {
        val repository = FakeAnimeRepository().apply {
            throwOnObserveWatchingList = IllegalStateException("watching load failed")
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

    private fun createViewModel(repository: FakeAnimeRepository): MyListViewModel {
        return MyListViewModel(
            observeWatchingList = ObserveWatchingListUseCase(repository),
            observePlannedList = ObservePlannedListUseCase(repository),
            observeCompletedList = ObserveCompletedListUseCase(repository)
        )
    }
}
