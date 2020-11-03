package mg.maniry.tenymana.ui.game

import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
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
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject

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
        val inUserRepo: UserRepo by inject()
        val userRepo = inUserRepo as UserRepoMock
        userRepo.userM.postValue(User("1", ""))
        // Puzzle builder
        val puzzle = LinkClearPuzzleMock(BibleVerse.fromText("Matio", 1, 10, "Ny")).apply {
            proposeFn.mockReturnValue(true)
            completed = true
        }
        val puzzleBuilder: PuzzleBuilder by inject()
        (puzzleBuilder as PuzzleBuilderMock).linkClearFn.mockReturnValue(puzzle)
        // Bible repo
        val inBibleRepo: BibleRepo by inject()
        val bibleRepo = inBibleRepo as BibleRepoMock
        bibleRepo.getSingleFn.mockReturnValue(BibleVerse.fromText("Matio", 1, 10, "Ny"))
        // game repo
        val inGameRepo: GameRepo by inject()
        val gameRepo = inGameRepo as GameRepoMock
        gameRepo.sessionsM.postValue(
            listOf(
                Session(
                    Journey(
                        "11",
                        title = "Journey 1",
                        description = "Long text ..",
                        paths = listOf(
                            Path("Path 0", "...", "Matio", 1, 10, 20),
                            Path("Path 1", "...", "Jaona", 1, 1, 10)
                        )
                    ),
                    Progress("11", totalScore = 5)
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
        )
        ActivityScenario.launch(MainActivity::class.java)
        // Init repositories
        assertThat(bibleRepo.setupFn.called).isTrue()
        assertThat(userRepo.setupFn.called).isTrue()
        // Go to game screen
        clickView(R.id.goToGameBtn)
        shouldBeVisible(R.id.gamesList)
        // go to paths screen
        clickView(R.id.gameListItem, 0)
        shouldBeVisible(R.id.pathsScreen)
        // Go to puzzle screen:
        clickView(R.id.pathsNextBtn)
        //  - load verse and init puzzle
        assertThat(bibleRepo.getSingleFn.calledWith("Matio", 1, 10)).isTrue()
        //  - go to puzzle screen
        shouldBeVisible(R.id.puzzleScreen)
        // Display headers
        shouldHaveText(R.id.puzzleHeaderVerseDisplay, text = "Matio 1:10")
        shouldHaveText(R.id.puzzleHeaderScore, text = "5")
        // Open LinkClear fragment
        shouldBeVisible(R.id.linkClearPuzzle)
        // Propose & complete
        swipeRight(R.id.charsGridInput)
        // On solution screen -> tap next -> load next verse + display puzzle screen
        shouldBeVisible(R.id.solutionScreen)
        clickView(R.id.solutionSaveAndContinueBtn)
        shouldBeVisible(R.id.puzzleScreen)
        assertThat(bibleRepo.getSingleFn.calledWith("Matio", 1, 11)).isTrue()
    }
}
