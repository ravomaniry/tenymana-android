package mg.maniry.tenymana.ui.game

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import mg.maniry.tenymana.gameLogic.linkClear.LinkClearPuzzle
import mg.maniry.tenymana.gameLogic.models.BibleVerse
import mg.maniry.tenymana.gameLogic.models.Puzzle
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
                Journey(
                    id = "ab",
                    paths = listOf(
                        Path("Path 1", "", "Matio", 1, 1, 3),
                        Path("Path 2", "", "Marka", 1, 1, 1)
                    )
                ),
                Progress("ab")
            )
            val sessions = MutableLiveData<List<Session>>()
            val gameRepo: GameRepo = mock {
                on { this.sessions } doReturn sessions
                onBlocking { initialize("1") } doAnswer {
                    sessions.postValue(listOf(session))
                }
            }
            // bible repo
            val verses = mapOf(
                Pair("Matio/1/1", BibleVerse.fromText("Matio", 1, 1, "Abc de")),
                Pair("Matio/1/2", BibleVerse.fromText("Matio", 1, 2, "fgh ij")),
                Pair("Matio/1/3", BibleVerse.fromText("Matio", 1, 3, "jkl")),
                Pair("Marka/1/1", BibleVerse.fromText("Marka", 1, 1, "fgh"))
            )
            val bibleRepo: BibleRepo = mock {
                onBlocking { getSingle(any(), any(), any()) } doAnswer {
                    verses["${it.arguments[0]}/${it.arguments[1]}/${it.arguments[2]}"]
                }
            }
            val random = RandomImpl()
            val viewModel =
                GameViewModel(appViewModel, userRepo, gameRepo, bibleRepo, random, TestDispatchers)
            // 1- Init sessions list
            assertThat(viewModel.sessions.value).isEqualTo(listOf(session))
            // 2- Select session and resume
            viewModel.onSessionClick(session)
            viewModel.continueSession()
            assertThat(viewModel.puzzle.value).isNotNull()
            // 4- Increment score & complete: go to SOLUTION screen
            viewModel.puzzle.value?.setScore(20)
            viewModel.onPuzzleCompleted()
            assertThat(viewModel.screen.value).isEqualTo(Screen.PUZZLE_SOLUTION)
            assertThat(viewModel.shouldNavigate).isTrue()
            // 5- SaveAndContinue: update progress, save, load nextverse, go to puzzle screen
            viewModel.saveAndContinue()
            var progress = Progress("ab", totalScore = 20, scores = listOf(listOf(20)))
            verifyOnce(gameRepo).saveProgress(progress)
            assertThat(viewModel.puzzle.value?.verse).isEqualTo(verses["Matio/1/2"])
            assertThat(viewModel.screen.value).isEqualTo(Screen.PUZZLE)
            assertThat(viewModel.session.value!!.progress).isEqualTo(progress)
            assertThat(viewModel.puzzle.value?.score).isEqualTo(0)
            assertThat(viewModel.shouldNavigate).isTrue()
            // Next once again
            viewModel.puzzle.value?.setScore(40)
            viewModel.onPuzzleCompleted()
            viewModel.saveAndContinue()
            progress = Progress("ab", totalScore = 60, scores = listOf(listOf(20, 40)))
            verifyOnce(gameRepo).saveProgress(progress)
            assertThat(viewModel.puzzle.value?.verse).isEqualTo(verses["Matio/1/3"])
            assertThat(viewModel.screen.value).isEqualTo(Screen.PUZZLE)
            assertThat(viewModel.session.value!!.progress).isEqualTo(progress)
            assertThat(viewModel.puzzle.value?.score).isEqualTo(0)
            assertThat(viewModel.shouldNavigate).isTrue()
            // Complete path
            val prevPzz = viewModel.puzzle.value
            viewModel.puzzle.value?.setScore(10)
            viewModel.onPuzzleCompleted()
            viewModel.saveAndContinue()
            progress = Progress("ab", totalScore = 70, scores = listOf(listOf(20, 40, 10)))
            verifyOnce(gameRepo).saveProgress(progress)
            assertThat(viewModel.screen.value).isEqualTo(Screen.PATHS_LIST)
            assertThat(viewModel.puzzle.value).isEqualTo(prevPzz)
            assertThat(viewModel.shouldNavigate).isTrue()
            // resume on paths screen load verse
            viewModel.continueSession()
            assertThat(viewModel.puzzle.value?.verse).isEqualTo(verses["Marka/1/1"])
            assertThat(viewModel.session.value!!.progress).isEqualTo(progress)
            assertThat(viewModel.puzzle.value?.score).isEqualTo(0)
            assertThat(viewModel.shouldNavigate).isTrue()
            // Complete the journey
            viewModel.puzzle.value?.setScore(30)
            viewModel.onPuzzleCompleted()
            viewModel.saveAndContinue()
            progress = Progress(
                "ab",
                totalScore = 100,
                scores = listOf(listOf(20, 40, 10), listOf(30))
            )
            verifyOnce(gameRepo).saveProgress(progress)
            assertThat(viewModel.puzzle.value).isNull()
            assertThat(viewModel.screen.value).isEqualTo(Screen.JOURNEY_COMPLETE)
            assertThat(viewModel.shouldNavigate).isTrue()
        }
    }

    @Test
    fun resumeNewGame() {
        testResume(
            paths = listOf(
                Path("path 0", "", "Jaona", 2, 2, 6),
                Path("path 0", "", "Marka", 1, 1, 10)
            ),
            scores = listOf(),
            firstVerse = BibleVerse.fromText("Jaona", 2, 2, "")
        )
    }

    @Test
    fun resumeUncompletePath_path0() {
        testResume(
            paths = listOf(
                Path("path 0", "", "Jaona", 2, 2, 6),
                Path("path 0", "", "Marka", 1, 1, 10)
            ),
            scores = listOf(listOf(2, 3)),
            firstVerse = BibleVerse.fromText("Jaona", 2, 4, "")
        )
    }

    @Test
    fun resumeUncompletePath_path1() {
        testResume(
            paths = listOf(
                Path("path 0", "", "Jaona", 2, 2, 6),
                Path("path 0", "", "Marka", 1, 1, 10)
            ),
            scores = listOf(listOf(2, 3, 4, 5, 6), listOf(1)),
            firstVerse = BibleVerse.fromText("Marka", 1, 2, "")
        )
    }

    @Test
    fun resumeNewPath() {
        testResume(
            paths = listOf(
                Path("path 0", "", "Jaona", 2, 2, 6),
                Path("path 0", "", "Marka", 1, 1, 10)
            ),
            scores = listOf(listOf(2, 3, 4, 5, 6)),
            firstVerse = BibleVerse.fromText("Marka", 1, 1, "")
        )
    }

    @Test
    fun resumeCompletedGame() {
        testResume(
            paths = listOf(
                Path("path 0", "", "Jaona", 2, 2, 6),
                Path("path 0", "", "Marka", 1, 1, 3)
            ),
            scores = listOf(listOf(1, 2, 3, 4, 5), listOf(1, 2, 3)),
            firstVerse = BibleVerse.fromText("Jaona", 2, 2, "")
        )
        testResume(
            paths = listOf(
                Path("path 0", "", "Jaona", 1, 1, 5),
                Path("path 0", "", "Marka", 1, 4, 6)
            ),
            scores = listOf(listOf(1, 2, 3, 4, 5), listOf(1, 2, 3)),
            firstVerse = BibleVerse.fromText("Jaona", 1, 1, "")
        )
    }

    private fun testResume(
        paths: List<Path>,
        scores: List<List<Int>>,
        firstVerse: BibleVerse
    ) {
        runBlocking {
            // App viewmodel
            val appViewModel: AppViewModel = mock {
                on { this.screen } doReturn MutableLiveData(Screen.GAMES_LIST)
            }
            // User repo
            val user = MutableLiveData<User>(User("1", "abc"))
            val userRepo: UserRepo = mock {
                on { this.user } doReturn user
            }
            // Game repo
            val session = Session(
                Journey(id = "ab", paths = paths),
                Progress("ab", totalScore = 80, scores = scores)
            )
            val sessions = MutableLiveData<List<Session>>()
            val gameRepo: GameRepo = mock {
                on { this.sessions } doReturn sessions
                onBlocking { initialize("1") } doAnswer { sessions.postValue(listOf(session)) }
            }
            // bible repo
            val bibleRepo: BibleRepo = mock {
                onBlocking { getSingle(any(), any(), any()) } doReturn firstVerse
            }
            val random = RandomImpl()
            val viewModel =
                GameViewModel(appViewModel, userRepo, gameRepo, bibleRepo, random, TestDispatchers)
            viewModel.apply {
                onSessionClick(session)
                continueSession()
            }
            // query correct verse
            verifyOnce(bibleRepo).getSingle(firstVerse.book, firstVerse.chapter, firstVerse.verse)
        }
    }

    private fun Puzzle.setScore(n: Int) {
        (this as LinkClearPuzzle).score = n
    }
}
