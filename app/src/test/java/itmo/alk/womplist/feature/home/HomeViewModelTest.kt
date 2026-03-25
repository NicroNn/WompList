package itmo.alk.womplist.feature.home

import app.cash.turbine.test
import itmo.alk.womplist.core.model.Anime
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
class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `init observes all anime`() = runTest {
        val animeList = listOf(
            testAnime(id = 1, name = "Naruto"),
            testAnime(id = 2, name = "Bleach")
        )
        val repository = FakeAnimeRepository(initialAllAnime = animeList)

        val viewModel = HomeViewModel(repository)
        advanceUntilIdle()

        assertEquals(animeList, viewModel.state.value.allAnime)
        assertEquals(animeList, viewModel.state.value.displayedList)
    }

    @Test
    fun `search intent updates results`() = runTest {
        val repository = FakeAnimeRepository(
            initialAllAnime = listOf(
                testAnime(id = 1, name = "Naruto"),
                testAnime(id = 2, name = "Bleach")
            )
        )
        val viewModel = HomeViewModel(repository)

        viewModel.onIntent(HomeIntent.SearchChanged("nar"))
        advanceUntilIdle()

        assertEquals("nar", viewModel.state.value.searchQuery)
        assertEquals(1, viewModel.state.value.searchResults.size)
        assertTrue(viewModel.state.value.searchResults.first().name.contains("Naruto"))
        assertEquals(false, viewModel.state.value.isSearching)
    }

    @Test
    fun `open title sends navigation effect`() = runTest {
        val viewModel = HomeViewModel(FakeAnimeRepository())

        viewModel.effects.test {
            viewModel.onIntent(HomeIntent.OpenTitle(42L))
            assertEquals(HomeEffect.NavigateToTitle(42L), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
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
}

