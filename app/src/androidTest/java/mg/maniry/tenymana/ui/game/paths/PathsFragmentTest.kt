package mg.maniry.tenymana.ui.game.paths

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.MutableLiveData
import androidx.test.ext.junit.runners.AndroidJUnit4
import mg.maniry.tenymana.R
import mg.maniry.tenymana.gameLogic.models.Score
import mg.maniry.tenymana.helpers.*
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
import org.mockito.Mockito.*

@RunWith(AndroidJUnit4::class)
class PathsFragmentTest : KoinTest {
    @Before
    fun setup() {
        setupTestKoin()
    }

    @After
    fun cleanup() {
        stopKoin()
    }

    @Test
    fun newGame_interactions() {
        val session = Session(
            Journey(
                "11",
                "Title",
                "Description ...",
                listOf(
                    Path("Path 0", "...0", "Matio", 1, 1, 15),
                    Path("Path 1", "...0", "Marka", 1, 10, 20)
                )
            ),
            Progress("11")
        )
        // Initial render
        val gameVM = testPathsList(session, showLeftBtn = false, showRightBtn = true, pathIndex = 0)
        // Select path 2 and click on continue button and verse item
        clearInvocations(gameVM)
        clickView(R.id.pathsScreenRightBtn)
        shouldHaveText(R.id.pathsScreenPathTitle, text = session.journey.paths[1].name)
        shouldBeVisible(R.id.pathsScreenLeftBtn)
        shouldBeInvisible(R.id.pathsScreenRightBtn)
        clickView(R.id.pathsScreenContinueBtn)
        verifyOnce(gameVM).onPathSelected(1, null)
        clickView(R.id.pathsScreenVerse, 0)
        verifyOnce(gameVM).onPathSelected(1, 0)
        // Go back to path 1 && continie && select
        clearInvocations(gameVM)
        clickView(R.id.pathsScreenLeftBtn)
        shouldHaveText(R.id.pathsScreenPathTitle, text = session.journey.paths[0].name)
        clickView(R.id.pathsScreenContinueBtn)
        verifyOnce(gameVM).onPathSelected(0, null)
        clickView(R.id.pathsScreenVerse, 2)
        verifyOnce(gameVM).onPathSelected(0, 2)
    }

    @Test
    fun newGameOnePath() {
        val session = Session(
            Journey(
                "11",
                "Title",
                "Description ...",
                listOf(Path("Path 0", "...0", "Matio", 1, 1, 15))
            ),
            Progress("11")
        )
        testPathsList(session, showLeftBtn = false, showRightBtn = false, pathIndex = 0)
    }

    @Test
    fun existingGameOnePath() {
        val session = Session(
            Journey(
                "11",
                "Title",
                "Description ...",
                listOf(Path("Path 0", "...0", "Matio", 1, 1, 15))
            ),
            Progress(
                "11",
                totalScore = 10,
                scores = listOf(
                    listOf(Score(10, 1), Score(11, 2), Score(12, 3))
                )
            )
        )
        testPathsList(session, showLeftBtn = false, showRightBtn = false, pathIndex = 0)
    }

    @Test
    fun existingGameMultiPath_resumeAtPathOne() {
        val session = Session(
            Journey(
                "11",
                "Title",
                "Description ...",
                listOf(
                    Path("Path 0", "...0", "Matio", 1, 1, 3),
                    Path("Path 1", "...0", "Matio", 1, 4, 15)
                )
            ),
            Progress(
                "11",
                totalScore = 10,
                scores = listOf(
                    listOf(Score(10, 1), Score(11, 2), Score(12, 3))
                )
            )
        )
        testPathsList(session, showLeftBtn = true, showRightBtn = false, pathIndex = 1)
    }

    @Test
    fun existingGameMultiPath_resumeAtPathOne_1pariallyDone() {
        val session = Session(
            Journey(
                "11",
                "Title",
                "Description ...",
                listOf(
                    Path("Path 0", "...0", "Matio", 1, 1, 3),
                    Path("Path 1", "...0", "Matio", 1, 4, 10),
                    Path("Path 1", "...0", "Matio", 1, 11, 15)
                )
            ),
            Progress(
                "11",
                totalScore = 10,
                scores = listOf(
                    listOf(Score(10, 1), Score(11, 2), Score(12, 3)),
                    listOf(Score(13, 1))
                )
            )
        )
        testPathsList(session, showLeftBtn = true, showRightBtn = true, pathIndex = 1)
    }

    private fun testPathsList(
        session: Session,
        showLeftBtn: Boolean,
        showRightBtn: Boolean,
        pathIndex: Int
    ): GameViewModel {
        val sharedViewModel: SharedViewModels by inject()
        lateinit var fragment: Fragment
        val gameViewModel = mock(GameViewModel::class.java).apply {
            `when`(this.sessions).thenReturn(MutableLiveData(listOf(session)))
            `when`(this.session).thenReturn(MutableLiveData(session))
        }
        sharedViewModel.game = gameViewModel
        val factory = object : FragmentFactory() {
            override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
                fragment = PathsFragment()
                return fragment
            }
        }
        launchFragmentInContainer<PathsFragment>(null, R.style.AppTheme, factory)
        shouldHaveText(R.id.pathsScreenJourneyTitle, text = session.journey.title)
        shouldHaveText(R.id.pathsScreenJourneyDescription, text = session.journey.description)
        // buttons
        if (showLeftBtn) {
            shouldBeVisible(R.id.pathsScreenLeftBtn)
        } else {
            shouldBeInvisible(R.id.pathsScreenLeftBtn)
        }
        if (showRightBtn) {
            shouldBeVisible(R.id.pathsScreenRightBtn)
        } else {
            shouldBeInvisible(R.id.pathsScreenRightBtn)
        }
        // Active path
        val path = session.journey.paths[pathIndex]
        shouldHaveText(R.id.pathsScreenPathTitle, text = path.name)
        shouldHaveText(R.id.pathsScreenPathChapter, text = "${path.book} ${path.chapter}")
        // Click on continue button
        clickView(R.id.pathsScreenContinueBtn)
        verifyOnce(gameViewModel).onPathSelected(pathIndex, null)
        // Clicks on verses
        for (verse in path.start..path.end) {
            val index = verse - path.start
            clickView(R.id.pathsScreenVerse, index)
            shouldHaveText(R.id.pathsScreenVerseNumber, index, text = verse.toString())
            verifyOnce(gameViewModel).onPathSelected(pathIndex, index)
        }
        return gameViewModel
    }
}
