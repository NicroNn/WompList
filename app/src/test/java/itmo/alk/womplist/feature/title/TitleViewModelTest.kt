package itmo.alk.womplist.feature.title

import app.cash.turbine.test
import itmo.alk.womplist.core.model.Anime
import itmo.alk.womplist.data.repository.AnimeStatus
import itmo.alk.womplist.testutil.FakeAnimeRepository
import itmo.alk.womplist.testutil.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TitleViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `initialize loads title and recommendations`() = runTest {
        val anime = testAnime(10L, "One Piece")
        val repository = FakeAnimeRepository(
            initialAllAnime = listOf(
                anime,
                testAnime(11L, "Bleach"),
                testAnime(12L, "Naruto"),
                testAnime(13L, "JJK")
            )
        )
        repository.putStatus(10L, AnimeStatus.WATCHING)
        repository.putRating(10L, 9)

        val viewModel = TitleViewModel(repository)
        viewModel.onIntent(TitleIntent.Initialize(10L))
        advanceUntilIdle()

        with(viewModel.state.value) {
            assertFalse(isLoading)
            assertEquals(anime, this.anime)
            assertEquals(AnimeStatus.WATCHING, currentStatus)
            assertEquals(9, currentUserRating)
            assertEquals(3, recommendations.size)
        }
    }

    @Test
    fun `set status updates state and closes dialog`() = runTest {
        val anime = testAnime(20L, "Attack on Titan")
        val repository = FakeAnimeRepository(initialAllAnime = listOf(anime))

        val viewModel = TitleViewModel(repository)
        viewModel.onIntent(TitleIntent.Initialize(20L))
        viewModel.onIntent(TitleIntent.OpenStatusDialog)
        viewModel.onIntent(TitleIntent.SetStatus(AnimeStatus.COMPLETED))
        advanceUntilIdle()

        assertEquals(AnimeStatus.COMPLETED, viewModel.state.value.currentStatus)
        assertFalse(viewModel.state.value.isStatusDialogVisible)
        assertEquals(20L to AnimeStatus.COMPLETED, repository.addedToList)
    }

    @Test
    fun `save rating persists value and closes overlay`() = runTest {
        val anime = testAnime(30L, "Frieren")
        val repository = FakeAnimeRepository(initialAllAnime = listOf(anime))

        val viewModel = TitleViewModel(repository)
        viewModel.onIntent(TitleIntent.Initialize(30L))
        viewModel.onIntent(TitleIntent.OpenRatingOverlay)
        viewModel.onIntent(TitleIntent.SaveRating(8))
        advanceUntilIdle()

        assertEquals(8, viewModel.state.value.currentUserRating)
        assertFalse(viewModel.state.value.isRatingOverlayVisible)
        assertEquals(30L to 8, repository.savedRating)
    }

    @Test
    fun `open title sends navigation effect`() = runTest {
        val viewModel = TitleViewModel(FakeAnimeRepository())

        viewModel.effects.test {
            viewModel.onIntent(TitleIntent.OpenTitle(77L))
            assertEquals(TitleEffect.NavigateToTitle(77L), awaitItem())
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
            episodes = 24,
            episodesAired = 24,
            status = "released",
            genres = listOf("Fantasy"),
            score = 9.0,
            year = 2024
        )
    }
}


