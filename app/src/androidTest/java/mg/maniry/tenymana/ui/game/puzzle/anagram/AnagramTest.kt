package mg.maniry.tenymana.ui.game.puzzle.anagram

import androidx.lifecycle.MutableLiveData
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import mg.maniry.tenymana.MainActivity
import mg.maniry.tenymana.R
import mg.maniry.tenymana.gameLogic.anagram.AnagramPuzzle
import mg.maniry.tenymana.gameLogic.models.BibleVerse
import mg.maniry.tenymana.gameLogic.shared.puzzleBuilder.PuzzleBuilder
import mg.maniry.tenymana.helpers.assertShouldBeVisible
import mg.maniry.tenymana.helpers.clickView
import mg.maniry.tenymana.helpers.setupTestKoin
import mg.maniry.tenymana.helpers.whenever
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
class AnagramTest : KoinTest {
    @Before
    fun iniKoin() {
        setupTestKoin()
    }

    @After
    fun cleanup() {
        stopKoin()
    }

    @Test
    fun runWithoutCrash() {
        // user mock
        val userRepo: UserRepo by inject()
        whenever(userRepo.user).thenReturn(MutableLiveData(User("1", "")))
        // Puzzle builder
        val verse = BibleVerse.fromText("Matio", 1, 10, "Ny filazana ny razan'i Jesosy Kristy")
        val puzzle = AnagramPuzzle.build(verse)
        val puzzleBuilder: PuzzleBuilder by inject()
        whenever(puzzleBuilder.random(verse)).thenReturn(puzzle)
        // Bible repo
        val bibleRepo: BibleRepo by inject()
        runBlocking { whenever(bibleRepo.getSingle("Matio", 1, 10)).thenReturn(verse) }
        // game repo
        val gameRepo: GameRepo by inject()
        val sessions = listOf(
            Session(
                Journey(
                    "11",
                    paths = listOf(Path("Path 0", "", "Matio", 1, 10, 20))
                ),
                Progress("11", totalScore = 20)
            )
        )
        whenever(gameRepo.sessions).thenReturn(MutableLiveData(sessions))
        // Run game navigate to puzzle screen
        runBlocking {
            ActivityScenario.launch(MainActivity::class.java)
            // -> Game screen
            clickView(R.id.goToGameBtn)
            assertShouldBeVisible(R.id.gamesList)
            // -> Paths screen
            clickView(R.id.gameListItem, 0)
            assertShouldBeVisible(R.id.pathsScreen)
            // -> Journey screen:
            clickView(R.id.pathsScreenContinueBtn)
            //  -> Path details screen -> auto click button
            assertShouldBeVisible(R.id.anagramBody)
            // use bonus
            clickView(R.id.anagramBonusOneBtn)
            delay(1000)
            // help
            clickView(R.id.puzzlHelpBtn)
            assertShouldBeVisible(R.id.helpAnagram)
            clickView(R.id.closeHelpBtn)
            assertShouldBeVisible(R.id.anagramBody)
        }
    }
}
