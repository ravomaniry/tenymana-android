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
import mg.maniry.tenymana.ui.app.SharedViewModels
import mg.maniry.tenymana.ui.game.GameViewModel
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import org.mockito.Mockito.clearInvocations
import org.mockito.Mockito.mock

@RunWith(AndroidJUnit4::class)
class SolutionTest : KoinTest {
    @Before
    fun before() {
        setupTestKoin()
    }

    @After
    fun after() {
        stopKoin()
    }

    @Test
    fun noExpand_twoStars() {
        val session = newSession(Path("path0", book = "Marka", chapter = 2, start = 10, end = 20))
        val pos = SessionPosition(session, isCompleted = false, pathIndex = 0, verseIndex = 0)
        val activeVerse = BibleVerse.fromText("Marka", 2, 10, "Abcd ef")
        val score = 4
        val stars = 2
        testSolutionScreen(session, pos, activeVerse, score, stars, canExpand = false)
    }

    @Test
    fun noExpand_oneStar() {
        val session = newSession(Path("path0", book = "Marka", chapter = 2, start = 10, end = 20))
        val pos = SessionPosition(session, isCompleted = false, pathIndex = 0, verseIndex = 0)
        val activeVerse = BibleVerse.fromText("Marka", 2, 10, "Abcd ef")
        val score = 1
        val stars = 1
        testSolutionScreen(session, pos, activeVerse, score, stars, canExpand = false)
    }

    @Test
    fun expand_threeStars() {
        val session = newSession(Path("path0", book = "Matio", chapter = 10, start = 2, end = 10))
        val pos = SessionPosition(session, isCompleted = false, pathIndex = 0, verseIndex = 4)
        val activeVerse = BibleVerse.fromText("Matio", 10, 6, "Abcd ef")
        val score = 100
        val stars = 3
        val verses = (2..6).map { BibleVerse.fromText("Matio", 10, it, "verse $it") }
        testSolutionScreen(session, pos, activeVerse, score, stars, verses, canExpand = true)
    }

    private fun testSolutionScreen(
        session: Session,
        pos: SessionPosition,
        activeVerse: BibleVerse,
        score: Int,
        stars: Int,
        verses: List<BibleVerse> = emptyList(),
        canExpand: Boolean = false
    ) {
        val sharedViewModels: SharedViewModels by inject()
        val gameViewModel = mock(GameViewModel::class.java)
        sharedViewModels.game = gameViewModel
        // session
        whenever(gameViewModel.session).thenReturn(MutableLiveData(session))
        whenever(gameViewModel.position).thenReturn(pos)
        // puzzle
        val puzzle = mock(Puzzle::class.java)
        whenever(puzzle.verse).thenReturn(activeVerse)
        whenever(puzzle.score).thenReturn(MutableLiveData(score))
        whenever(gameViewModel.puzzle).thenReturn(MutableLiveData(puzzle))
        // repo
        val bibleRepo: BibleRepo by inject()
        var minV = 0
        var maxV = 0
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
                return SolutionFragment()
            }
        }
        launchFragmentInContainer<SolutionFragment>(null, R.style.AppTheme, factory)
        // 1- Display title + small view with verse inside
        assertShouldBeVisible(R.id.solutionScreenSmallView)
        assertShouldHaveText(
            R.id.solutionScreenTitle,
            text = "${activeVerse.book} ${activeVerse.chapter}:${activeVerse.verse}"
        )
        assertShouldHaveText(R.id.solutionScreenVerse, text = activeVerse.text)
        assertShouldBeInvisible(R.id.solutionScreenBigView)
        // 2- click on btn call viewModel's function
        clickView(R.id.solutionSaveAndContinueBtn)
        verifyOnce(gameViewModel).saveAndContinue()
        // Score && stars
        assertShouldHaveText(R.id.solutionScreenScore, text = "$score")
        assertShouldBeVisible(R.id.solutionScreenStar0)
        if (stars >= 2) {
            assertShouldBeVisible(R.id.solutionScreenStar1)
        } else {
            assertShouldBeInvisible(R.id.solutionScreenStar1)
        }
        if (stars >= 3) {
            assertShouldBeVisible(R.id.solutionScreenStar2)
        } else {
            assertShouldBeInvisible(R.id.solutionScreenStar2)
        }
        // Expand
        if (canExpand) {
            // Expand
            clearInvocations(gameViewModel)
            assertShouldBeVisible(R.id.solutionScreenExpandBtn)
            clickView(R.id.solutionScreenExpandBtn)
            // 1- fetch verses from bible repository
            runBlocking {
                verifyOnce(bibleRepo).get(activeVerse.book, activeVerse.chapter, minV, maxV)
            }
            // 2- update title
            assertShouldHaveText(
                R.id.solutionScreenTitle,
                text = "${activeVerse.book} ${activeVerse.chapter}"
            )
            // 3- show big view & items
            assertShouldBeInvisible(R.id.solutionScreenSmallView)
            assertShouldBeVisible(R.id.solutionScreenBigView)
            for (v in 2..6) {
                assertShouldHaveText(
                    R.id.solutionScreenVerseItem,
                    v - 2,
                    "$v- ${verses[v - 2].text}"
                )
            }
            // 4- click is available
            clickView(R.id.solutionSaveAndContinueBtn)
            verifyOnce(gameViewModel).saveAndContinue()
        } else {
            assertShouldBeInvisible(R.id.solutionScreenExpandBtn)
        }
    }

    private fun newSession(path: Path) = Session(
        Journey("ab", paths = listOf(path)),
        Progress("ab")
    )
}
