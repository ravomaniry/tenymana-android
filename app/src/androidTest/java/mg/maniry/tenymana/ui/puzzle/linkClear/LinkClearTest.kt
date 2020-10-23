package mg.maniry.tenymana.ui.puzzle.linkClear

import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import mg.maniry.tenymana.MainActivity
import mg.maniry.tenymana.R
import mg.maniry.tenymana.helpers.*
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
        val inUserRepo: UserRepo by inject()
        val inGameRepo: GameRepo by inject()
        val inRandom: Random by inject()
        val userRepo = inUserRepo as UserRepoMock
        val gameRepo = inGameRepo as GameRepoMock
        // random always pick first
        userRepo.userM.postValue(User("1", ""))
        val random = inRandom as RandomMock
        random.intFn.mockImplementation { it[0] as Int }
        @Suppress("unchecked_cast")
        random.fromFn.mockImplementation { (it[0] as List<Any>)[0] }
        gameRepo.sessionsM.postValue(
            listOf(
                Session(
                    Journey.empty("11").copy(
                        title = "Journey 1",
                        description = "Long text ..",
                        paths = listOf(
                            Path("Path0", "...", "Matio", 1, 1, 20)
                        )
                    ),
                    Progress.empty("11")
                ),
                Session(
                    Journey.empty("22").copy(title = "Journey 2"),
                    Progress.empty("22")
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
        // Go to puzzle screen
        clickView(R.id.pathsNextBtn)
        shouldBeVisible(R.id.puzzleScreen)
        //
    }
}
