package mg.maniry.tenymana.ui.game.puzzle.linkClear

import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import mg.maniry.tenymana.MainActivity
import mg.maniry.tenymana.R
import mg.maniry.tenymana.gameLogic.models.BibleVerse
import mg.maniry.tenymana.helpers.*
import mg.maniry.tenymana.repositories.BibleRepo
import mg.maniry.tenymana.repositories.GameRepo
import mg.maniry.tenymana.repositories.UserRepo
import mg.maniry.tenymana.repositories.models.*
import mg.maniry.tenymana.utils.Random
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject

@RunWith(AndroidJUnit4::class)
class LinkClearTest : KoinTest {
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
        // random (always pick first)
        val inRandom: Random by inject()
        val random = inRandom as RandomMock
        random.intFn.mockImplementation { it[0] as Int }
        @Suppress("unchecked_cast")
        random.fromFn.mockImplementation { (it[0] as List<Any>)[0] }
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
                    Journey.empty("11").copy(
                        title = "Journey 1",
                        description = "Long text ..",
                        paths = listOf(
                            Path("Path0", "...", "Matio", 1, 10, 20)
                        )
                    ),
                    Progress.empty("11").copy(totalScore = 5)
                ),
                Session(
                    Journey.empty("22").copy(
                        title = "Journey 2",
                        paths = listOf(Path("Path 1", "..", "Marka", 1, 1, 2))
                    ),
                    Progress.empty("22").copy(totalScore = 10, scores = listOf(10))
                )
            )
        )
        ActivityScenario.launch(MainActivity::class.java)
        // Go to game screen
        clickView(R.id.goToGameBtn)
        shouldBeVisible(R.id.gamesList)
        // go to paths screen
        clickView(R.id.gameListItem, 0)
        shouldBeVisible(R.id.pathsGrid)
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
    }
}
