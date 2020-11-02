package mg.maniry.tenymana.ui.game

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import mg.maniry.tenymana.gameLogic.linkClear.LinkClearPuzzle
import mg.maniry.tenymana.gameLogic.models.BibleVerse
import mg.maniry.tenymana.repositories.BibleRepo
import mg.maniry.tenymana.repositories.GameRepo
import mg.maniry.tenymana.repositories.UserRepo
import mg.maniry.tenymana.repositories.models.*
import mg.maniry.tenymana.ui.app.AppViewModel
import mg.maniry.tenymana.ui.app.Screen
import mg.maniry.tenymana.utils.RandomImpl
import mg.maniry.tenymana.utils.TestDispatchers
import mg.maniry.tenymana.utils.verifyOnce
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class GameViewModelTest {
    @get:Rule
    val liveDataRule = InstantTaskExecutorRule()

    @Test
    fun navigation() {
        runBlocking {
            // App viewmodel
            val screen = MutableLiveData<Screen>(Screen.GAMES_LIST)
            val appViewModel: AppViewModel = mock {
                on { this.screen } doReturn screen
            }
            // User repo
            val user = MutableLiveData<User>(User("1", "abc"))
            val userRepo: UserRepo = mock {
                on { this.user } doReturn user
            }
            // Game repo
            val session = Session(
                Journey.empty("ab").copy(paths = listOf(Path("Path 1", "", "Matio", 1, 1, 20))),
                Progress.empty("ab").copy()
            )
            val sessions = MutableLiveData<List<Session>>()
            val gameRepo: GameRepo = mock {
                on { this.sessions } doReturn sessions
                onBlocking { initialize("1") } doAnswer {
                    sessions.postValue(listOf(session))
                }
            }
            // bible repo
            val verse0 = BibleVerse.fromText("Matio", 1, 1, "Abc de")
            val verse1 = BibleVerse.fromText("Matio", 1, 2, "fgh ij")
            val bibleRepo: BibleRepo = mock {
                onBlocking { getSingle("Matio", 1, 1) } doReturn verse0
                onBlocking { getSingle("Matio", 1, 2) } doReturn verse1
            }
            val random = RandomImpl()
            val viewModel =
                GameViewModel(appViewModel, userRepo, gameRepo, bibleRepo, random, TestDispatchers)
            // 1- Init sessions list
            assertThat(viewModel.sessions.value).isEqualTo(listOf(session))
            // 2- Select session and resume
            viewModel.onSessionClick(session)
            viewModel.resumeSession()
            assertThat(viewModel.puzzle.value).isNotNull()
            // 4- Increment score & complete: go to SOLUTION screen
            (viewModel.puzzle.value as LinkClearPuzzle).score = 20
            viewModel.onPuzzleCompleted()
            assertThat(viewModel.screen.value).isEqualTo(Screen.PUZZLE_SOLUTION)
            // 5- SaveAndContinue: update progress, save, load nextverse, go to puzzle screen
            viewModel.saveAndContinue()
            verifyOnce(gameRepo).saveProgress(
                Progress.empty("ab").copy(totalScore = 20, scores = listOf(listOf(20)))
            )
            assertThat(viewModel.puzzle.value?.verse).isEqualTo(verse1)
            assertThat(viewModel.screen.value).isEqualTo(Screen.PUZZLE)
            assertThat(viewModel.session.value!!.progress).isEqualTo(
                Progress.empty("ab").copy(totalScore = 20, scores = listOf(listOf(20)))
            )
        }
    }
}
