package mg.maniry.tenymana.ui.game

import androidx.lifecycle.MutableLiveData
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import mg.maniry.tenymana.MainActivity
import mg.maniry.tenymana.R
import mg.maniry.tenymana.gameLogic.models.BibleVerse
import mg.maniry.tenymana.gameLogic.models.Score
import mg.maniry.tenymana.gameLogic.shared.puzzleBuilder.PuzzleBuilder
import mg.maniry.tenymana.helpers.*
import mg.maniry.tenymana.repositories.BibleRepo
import mg.maniry.tenymana.repositories.GameRepo
import mg.maniry.tenymana.repositories.UserRepo
import mg.maniry.tenymana.repositories.models.*
import mg.maniry.tenymana.ui.game.puzzle.PuzzleViewModel
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import org.mockito.Mockito.clearInvocations

@RunWith(AndroidJUnit4::class)
class GameTest : KoinTest {
    @Before
    fun iniKoin() {
        setupTestKoin()
    }

    @After
    fun cleanup() {
        stopKoin()
    }

    @Test
    fun navigation() {
        // user mock
        val userRepo: UserRepo by inject()
        whenever(userRepo.user).thenReturn(MutableLiveData(User("1", "")))
        // Puzzle builder
        val puzzleBuilder: PuzzleBuilder by inject()
        val math110 = BibleVerse.fromText("Matio", 1, 10, "Ny")
        val puzzle0 = LinkClearPuzzleMock(math110).apply {
            proposeFn.mockReturnValue(true)
            completed = true
        }
        whenever(puzzleBuilder.random(math110)).thenReturn(puzzle0)
        val math111 = BibleVerse.fromText("Matio", 1, 11, "Ny")
        val puzzle1 = LinkClearPuzzleMock(math111).apply {
            proposeFn.mockReturnValue(true)
            completed = true
        }
        whenever(puzzleBuilder.random(math111)).thenReturn(puzzle1)
        val math112 = BibleVerse.fromText("Matio", 1, 12, "Ny")
        val puzzle2 = LinkClearPuzzleMock(math112).apply {
            proposeFn.mockReturnValue(true)
            completed = true
        }
        whenever(puzzleBuilder.random(math112)).thenReturn(puzzle2)
        // Bible repo
        val bibleRepo: BibleRepo by inject()
        runBlocking {
            whenever(bibleRepo.getSingle("Matio", 1, 10)).thenReturn(math110)
            whenever(bibleRepo.getSingle("Matio", 1, 11)).thenReturn(math111)
            whenever(bibleRepo.getSingle("Matio", 1, 12)).thenReturn(math112)
            whenever(bibleRepo.get("Matio", 1, 10, 12)).thenReturn(
                listOf(math110, math111, math112)
            )
        }
        // game repo
        val gameRepo: GameRepo by inject()
        val sessions = listOf(
            Session(
                Journey(
                    "11",
                    title = "Journey 1",
                    description = "Long text ..",
                    paths = listOf(
                        Path("Path 0", "##Path1\n**title** __italic__ plain", "Matio", 1, 10, 12),
                        Path("Path 1", "##Path1\n**title** __italic__", "Jaona", 1, 1, 10)
                    )
                ),
                Progress("11", totalScore = 50)
            ),
            Session(
                Journey(
                    "22",
                    title = "Journey 2",
                    paths = listOf(Path("Path 1", "..", "Marka", 1, 1, 2))
                ),
                Progress("22", totalScore = 10, scores = listOf(listOf(Score(10, 3))))
            )
        )
        whenever(gameRepo.sessions).thenReturn(MutableLiveData(sessions))
        runBlocking {
            ActivityScenario.launch(MainActivity::class.java)
            // Init repositories
            verifyOnce(bibleRepo).setup()
            verifyOnce(userRepo).setup()
            // Go to game screen
            clickView(R.id.goToGameBtn)
            assertShouldBeVisible(R.id.gamesList)
            // go to paths screen
            clickView(R.id.gameListItem, 0)
            assertShouldBeVisible(R.id.pathsScreen)
            // Go to puzzle screen:
            clickView(R.id.pathsScreenContinueBtn)
            //  - load verse and init puzzle
            verifyOnce(bibleRepo).getSingle("Matio", 1, 10)
            //  - go to path details screen
            assertShouldBeVisible(R.id.pathDetailsScreen)
            assertShouldHaveText(R.id.pathDetailsVerseRef, text = "Matio 1:10-12")
            clickView(R.id.pathDetailsNextBtn)
            //  - go to puzzle screen
            assertShouldBeVisible(R.id.puzzleScreen)
            // Display headers
            assertShouldHaveText(R.id.puzzleHeaderVerseDisplay, text = "Matio 1:10")
            assertShouldHaveText(R.id.puzzleHeaderScore, text = "50")
            // Open LinkClear fragment
            assertShouldBeVisible(R.id.linkClearPuzzle)
            // Bonus
            clickView(R.id.puzzleBonusOneBtn)
            assertThat(puzzle0.useBonusOneFn.calledWith(PuzzleViewModel.bonusOnePrice)).isTrue()
            // Propose & complete
            swipeRight(R.id.charsGridInput)
            // On solution screen -> tap next -> load next verse + display puzzle screen
            assertShouldBeVisible(R.id.solutionScreen)
            clickView(R.id.solutionSaveAndContinueBtn)
            assertShouldBeVisible(R.id.puzzleScreen)
            verifyOnce(bibleRepo).getSingle("Matio", 1, 11)
            // Complete -> solution screen -> next: puzzle (1:12)
            swipeRight(R.id.charsGridInput)
            clickView(R.id.solutionSaveAndContinueBtn)
            assertShouldBeVisible(R.id.puzzleScreen)
            verifyOnce(bibleRepo).getSingle("Matio", 1, 12)
            // Complete path: Solution screen expanded -> next: path details -> next: paths list
            swipeRight(R.id.charsGridInput)
            assertShouldBeVisible(R.id.solutionScreenBigView)
            clickView(R.id.solutionSaveAndContinueBtn)
            clickView(R.id.pathDetailsNextBtn)
            assertShouldBeVisible(R.id.pathsScreen)
        }
    }

    @Test
    fun deleteJourney() {
        // user mock
        val userRepo: UserRepo by inject()
        whenever(userRepo.user).thenReturn(MutableLiveData(User("1", "")))
        // game repo
        val gameRepo: GameRepo by inject()
        val sessions = listOf(
            Session(
                Journey("j0"),
                Progress("11", totalScore = 50)
            ),
            Session(
                Journey("j1"),
                Progress("22", totalScore = 10, scores = listOf(listOf(Score(10, 3))))
            )
        )
        whenever(gameRepo.sessions).thenReturn(MutableLiveData(sessions))
        runBlocking {
            ActivityScenario.launch(MainActivity::class.java)
            // Go to game screen
            clickView(R.id.goToGameBtn)
            assertShouldBeVisible(R.id.gamesList)
            // No delete dialog
            assertShouldBeInvisible(R.id.deleteJourneyDialog)
            // delete -> cancel
            clickView(R.id.deleteJourneyBtn, 0)
            assertShouldBeVisible(R.id.deleteJourneyDialog)
            clickView(R.id.cancelDeleteJourneyBtn)
            assertShouldBeInvisible(R.id.deleteJourneyDialog)
            verifyNever(gameRepo).deleteJourney("j0")
            // delete -> confirm
            clearInvocations(gameRepo)
            clickView(R.id.deleteJourneyBtn, 1)
            clickView(R.id.confirmDeleteJourneyBtn)
            verifyOnce(gameRepo).deleteJourney("j1")
            verifyOnce(gameRepo).initialize("1")
            assertShouldBeInvisible(R.id.deleteJourneyDialog)
        }
    }
}
