package mg.maniry.tenymana.ui.puzzle.linkClear

import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import mg.maniry.tenymana.MainActivity
import mg.maniry.tenymana.R
import mg.maniry.tenymana.helpers.*
import mg.maniry.tenymana.repositories.GameRepo
import mg.maniry.tenymana.repositories.UserRepo
import mg.maniry.tenymana.repositories.models.Journey
import mg.maniry.tenymana.repositories.models.Progress
import mg.maniry.tenymana.repositories.models.Session
import mg.maniry.tenymana.repositories.models.User
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
        val userRepo = inUserRepo as UserRepoMock
        val gameRepo = inGameRepo as GameRepoMock
        userRepo.userM.postValue(User("1", ""))
        gameRepo.sessionsM.postValue(
            listOf(
                Session(
                    Journey.empty("11").copy(title = "Journey 1"),
                    Progress.empty("11")
                )
            )
        )
        ActivityScenario.launch(MainActivity::class.java)
        // Go to game screen
        clickView(R.id.goToGameBtn)
        shouldBeVisible(R.id.gamesList)
        // go to puzzle screen
        clickView(R.id.gameListItem)
        shouldBeVisible(R.id.puzzleScreen)
    }
}
