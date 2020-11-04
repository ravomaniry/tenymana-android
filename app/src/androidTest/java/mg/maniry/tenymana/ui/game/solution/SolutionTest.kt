package mg.maniry.tenymana.ui.game.solution

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.MutableLiveData
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import mg.maniry.tenymana.R
import mg.maniry.tenymana.gameLogic.models.BibleVerse
import mg.maniry.tenymana.gameLogic.models.Puzzle
import mg.maniry.tenymana.gameLogic.shared.session.SessionPosition
import mg.maniry.tenymana.helpers.*
import mg.maniry.tenymana.repositories.BibleRepo
import mg.maniry.tenymana.repositories.models.Journey
import mg.maniry.tenymana.repositories.models.Path
import mg.maniry.tenymana.repositories.models.Progress
import mg.maniry.tenymana.repositories.models.Session
import mg.maniry.tenymana.ui.game.GameViewModel
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*

@RunWith(AndroidJUnit4::class)
class SolutionTest {
    @Test
    fun noExpand() {
        val session = Session(
            Journey(
                "ab",
                paths = listOf(Path("path0", book = "Marka", chapter = 2, start = 10, end = 20))
            ),
            Progress("ab")
        )
        val pos = SessionPosition(session, isCompleted = false, pathIndex = 0, verseIndex = 0)
        val activeVerse = BibleVerse.fromText("Marka", 2, 10, "Abcd ef")
        testSolutionScreen(session, pos, activeVerse, canExpand = false)
    }

    @Test
    fun expand() {
        val session = Session(
            Journey(
                "ab",
                paths = listOf(Path("path0", book = "Matio", chapter = 10, start = 2, end = 10))
            ),
            Progress("ab")
        )
        val pos = SessionPosition(session, isCompleted = false, pathIndex = 0, verseIndex = 4)
        val activeVerse = BibleVerse.fromText("Matio", 10, 6, "Abcd ef")
        val verses = (2..6).map { BibleVerse.fromText("Matio", 10, it, "verse $it") }
        testSolutionScreen(session, pos, activeVerse, verses, canExpand = true)
    }

    private fun testSolutionScreen(
        session: Session,
        pos: SessionPosition,
        activeVerse: BibleVerse,
        verses: List<BibleVerse> = emptyList(),
        canExpand: Boolean = false
    ) {
        val gameViewModel = mock(GameViewModel::class.java)
        // session
        whenever(gameViewModel.session).thenReturn(MutableLiveData(session))
        whenever(gameViewModel.position).thenReturn(pos)
        // puzzle
        val puzzle = mock(Puzzle::class.java)
        whenever(puzzle.verse).thenReturn(activeVerse)
        whenever(gameViewModel.puzzle).thenReturn(MutableLiveData(puzzle))
        // repo
        var minV = 0
        var maxV = 0
        val bibleRepo = mock(BibleRepo::class.java)
        if (verses.isNotEmpty()) {
            minV = verses[0].verse
            maxV = verses.last().verse
            runBlocking {
                whenever(bibleRepo.get(activeVerse.book, activeVerse.chapter, minV, maxV))
                    .thenReturn(verses)
            }
        }
        // render
        val factory = object : FragmentFactory() {
            override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
                return SolutionFragment(gameViewModel, bibleRepo)
            }
        }
        launchFragmentInContainer<SolutionFragment>(null, R.style.AppTheme, factory)
        // 1- Display title + small view with verse inside
        shouldBeVisible(R.id.solutionScreenSmallView)
        shouldHaveText(
            R.id.solutionScreenTitle,
            text = "${activeVerse.book} ${activeVerse.chapter}:${activeVerse.verse}"
        )
        shouldHaveText(R.id.solutionScreenVerse, text = activeVerse.text)
        shouldBeInvisible(R.id.solutionScreenBigView)
        // 2- click on btn call viewModel's function
        clickView(R.id.solutionSaveAndContinueBtn)
        verify(gameViewModel, times(1)).saveAndContinue()
        if (canExpand) {
            // Expand
            clearInvocations(gameViewModel)
            shouldBeVisible(R.id.solutionScreenExpandBtn)
            clickView(R.id.solutionScreenExpandBtn)
            // 1- fetch verses from bible repository
            runBlocking {
                verify(bibleRepo, times(1)).get(activeVerse.book, activeVerse.chapter, minV, maxV)
            }
            // 2- update title
            shouldHaveText(
                R.id.solutionScreenTitle,
                text = "${activeVerse.book} ${activeVerse.chapter}"
            )
            // 3- show big view & items
            shouldBeInvisible(R.id.solutionScreenSmallView)
            shouldBeVisible(R.id.solutionScreenBigView)
            for (v in 2..6) {
                shouldHaveText(R.id.solutionScreenVerseItem, v - 2, "$v- ${verses[v - 2].text}")
            }
            // 4- click is available
            clickView(R.id.solutionSaveAndContinueBtn)
            verify(gameViewModel, times(1)).saveAndContinue()
        } else {
            shouldBeInvisible(R.id.solutionScreenExpandBtn)
        }
    }
}
