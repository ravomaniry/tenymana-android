package mg.maniry.tenymana.ui.game

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import mg.maniry.tenymana.gameLogic.linkClear.LinkClearPuzzle
import mg.maniry.tenymana.gameLogic.models.BibleVerse
import mg.maniry.tenymana.gameLogic.models.Score
import mg.maniry.tenymana.gameLogic.shared.puzzleBuilder.PuzzleBuilder
import mg.maniry.tenymana.repositories.BibleRepo
import mg.maniry.tenymana.repositories.GameRepo
import mg.maniry.tenymana.repositories.UserRepo
import mg.maniry.tenymana.repositories.models.*
import mg.maniry.tenymana.ui.app.AppViewModel
import mg.maniry.tenymana.ui.app.Screen
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
                Pair("Matio/1/3", BibleVerse.fromText("Matio", 1, 3, "jkl km")),
                Pair("Marka/1/1", BibleVerse.fromText("Marka", 1, 1, "fgh ij"))
            )
            val bibleRepo: BibleRepo = mock {
                onBlocking { getSingle(any(), any(), any()) } doAnswer {
                    verses["${it.arguments[0]}/${it.arguments[1]}/${it.arguments[2]}"]
                }
            }
            // Puzzle builder
            val score = MutableLiveData(0)
            val puzzle: LinkClearPuzzle = mock {
                on { this.score } doAnswer { score }
                on { this.verse } doReturn BibleVerse.fromText("", 1, 1, "Abc de")
            }
            val puzzleBuilder: PuzzleBuilder = mock {
                on { this.random(any()) } doReturn puzzle
            }
            // test
            val viewModel =
                GameViewModelImpl(
                    appViewModel,
                    userRepo,
                    gameRepo,
                    bibleRepo,
                    puzzleBuilder,
                    TestDispatchers
                )
            // 1- Init sessions list
            assertThat(viewModel.sessions.value).isEqualTo(listOf(session))
            // 2- Select session and resume
            viewModel.onSessionClick(session)
            viewModel.onPathSelected(viewModel.position!!.pathIndex, null)
            assertThat(viewModel.screen.value).isEqualTo(Screen.PATH_DETAILS)
            viewModel.closePathDetails()
            assertThat(viewModel.screen.value).isEqualTo(Screen.PUZZLE)
            assertThat(viewModel.puzzle.value).isNotNull()
            // 4- Increment score (3 stars) & complete: go to SOLUTION screen
            score.postValue(20)
            viewModel.onPuzzleCompleted()
            assertThat(viewModel.screen.value).isEqualTo(Screen.PUZZLE_SOLUTION)
            assertThat(viewModel.shouldNavigate).isTrue()
            // 5- SaveAndContinue: update progress, save, load nextverse, go to puzzle screen
            viewModel.saveAndContinue()
            var progress = Progress(
                "ab",
                totalScore = 20,
                scores = listOf(listOf(Score(20, 3)))
            )
            verifyOnce(gameRepo).saveProgress(progress)
            verifyOnce(puzzleBuilder).random(verses["Matio/1/2"] ?: error("1"))
            assertThat(viewModel.screen.value).isEqualTo(Screen.PUZZLE)
            assertThat(viewModel.session.value!!.progress).isEqualTo(progress)
            assertThat(viewModel.shouldNavigate).isTrue()
            // Next once again: (1 star)
            score.postValue(2)
            viewModel.onPuzzleCompleted()
            viewModel.saveAndContinue()
            progress = Progress(
                "ab",
                totalScore = 20 + 2,
                scores = listOf(listOf(Score(20, 3), Score(2, 1)))
            )
            verifyOnce(gameRepo).saveProgress(progress)
            verifyOnce(puzzleBuilder).random(verses["Matio/1/3"] ?: error("1"))
            assertThat(viewModel.screen.value).isEqualTo(Screen.PUZZLE)
            assertThat(viewModel.session.value!!.progress).isEqualTo(progress)
            assertThat(viewModel.shouldNavigate).isTrue()
            // Complete path: 2 stars
            val prevPzz = viewModel.puzzle.value
            score.postValue(3)
            viewModel.onPuzzleCompleted()
            viewModel.saveAndContinue()
            progress = Progress(
                "ab",
                totalScore = 20 + 2 + 3,
                scores = listOf(listOf(Score(20, 3), Score(2, 1), Score(3, 2)))
            )
            verifyOnce(gameRepo).saveProgress(progress)
            assertThat(viewModel.screen.value).isEqualTo(Screen.PATHS_LIST)
            assertThat(viewModel.puzzle.value).isEqualTo(prevPzz)
            assertThat(viewModel.shouldNavigate).isTrue()
            // resume on paths screen load verse + go to path details
            viewModel.onPathSelected(viewModel.position!!.pathIndex, null)
            assertThat(viewModel.screen.value).isEqualTo(Screen.PATH_DETAILS)
            viewModel.closePathDetails()
            assertThat(viewModel.screen.value).isEqualTo(Screen.PUZZLE)
            verifyOnce(puzzleBuilder).random(verses["Marka/1/1"] ?: error("1"))
            assertThat(viewModel.session.value!!.progress).isEqualTo(progress)
            assertThat(viewModel.shouldNavigate).isTrue()
            // Complete the journey
            score.postValue(30)
            viewModel.onPuzzleCompleted()
            viewModel.saveAndContinue()
            progress = Progress(
                "ab",
                totalScore = 20 + 2 + 3 + 30,
                scores = listOf(
                    listOf(Score(20, 3), Score(2, 1), Score(3, 2)),
                    listOf(Score(30, 3))
                )
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
            firstVerse = BibleVerse.fromText("Jaona", 2, 2, ""),
            selections = listOf(
                Selection(0, 0, true, "Jaona", 2, 2),
                Selection(1, 0, true, "Marka", 1, 1),
                Selection(0, 1), Selection(0, 2), Selection(0, 3),
                Selection(1, 1), Selection(1, 2), Selection(1, 3)
            )
        )
    }

    @Test
    fun resumeUncompletePath_path0() {
        testResume(
            paths = listOf(
                Path("path 0", "", "Jaona", 2, 2, 6),
                Path("path 0", "", "Marka", 1, 1, 10)
            ),
            scores = listOf(scores(2, 3)),
            firstVerse = BibleVerse.fromText("Jaona", 2, 4, ""),
            selections = listOf(
                Selection(0, 0, true, "Jaona", 2, 2),
                Selection(0, 1, true, "Jaona", 2, 3),
                Selection(0, 2, true, "Jaona", 2, 4),
                Selection(1, 0, true, "Marka", 1, 1),
                Selection(0, 3), Selection(0, 4),
                Selection(1, 1), Selection(1, 2)
            )
        )
    }

    @Test
    fun resumeUncompletePath_path1() {
        testResume(
            paths = listOf(
                Path("path 0", "", "Jaona", 2, 2, 6),
                Path("path 0", "", "Marka", 1, 1, 10)
            ),
            scores = listOf(scores(2, 3, 4, 5, 6), scores(1)),
            firstVerse = BibleVerse.fromText("Marka", 1, 2, ""),
            selections = listOf(
                Selection(1, 1, true, "Marka", 1, 2), Selection(1, 2)
            )
        )
    }

    @Test
    fun resumeNewPath() {
        testResume(
            paths = listOf(
                Path("path 0", "", "Jaona", 2, 2, 6),
                Path("path 0", "", "Marka", 1, 1, 10)
            ),
            scores = listOf(scores(2, 3, 4, 5, 6)),
            firstVerse = BibleVerse.fromText("Marka", 1, 1, "")
        )
    }

    @Test
    fun resumeCompletedGameSinglePath() {
        testResume(
            paths = listOf(
                Path("path 0", "", "Jaona", 2, 2, 6)
            ),
            scores = listOf(scores(1, 2, 3, 4, 5)),
            firstVerse = BibleVerse.fromText("Jaona", 2, 2, ""),
            selections = listOf(
                Selection(0, 0, true, "Jaona", 2, 2),
                Selection(0, 3, true, "Jaona", 2, 5),
                Selection(0, 4, true, "Jaona", 2, 6)
            )
        )
    }

    @Test
    fun resumeCompletedGameMultiPaths() {
        testResume(
            paths = listOf(
                Path("path 0", "", "Jaona", 1, 1, 5),
                Path("path 0", "", "Marka", 1, 4, 6)
            ),
            scores = listOf(scores(1, 2, 3, 4, 5), scores(1, 2, 3)),
            firstVerse = BibleVerse.fromText("Jaona", 1, 1, ""),
            selections = listOf(
                Selection(0, 3, true, "Jaona", 1, 4),
                Selection(1, 0, true, "Marka", 1, 4),
                Selection(1, 2, true, "Marka", 1, 6)
            )
        )
    }

    private fun testResume(
        paths: List<Path>,
        scores: List<List<Score>>,
        firstVerse: BibleVerse,
        selections: List<Selection> = emptyList()
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
            val puzzle: LinkClearPuzzle = mock()
            val puzzleBuilder: PuzzleBuilder = mock {
                on { random(any()) } doReturn puzzle
            }
            val viewModel = GameViewModelImpl(
                appViewModel,
                userRepo,
                gameRepo,
                bibleRepo,
                puzzleBuilder,
                TestDispatchers
            )
            viewModel.onSessionClick(session)
            // Resme default path
            viewModel.onPathSelected(viewModel.position!!.pathIndex, null)
            verifyOnce(bibleRepo).getSingle(firstVerse.book, firstVerse.chapter, firstVerse.verse)
            clearInvocations(bibleRepo)
            // Select verses
            for (s in selections) {
                viewModel.onPathSelected(s.pathI, s.verseI)
                val pos = viewModel.position!!
                if (s.isValid) {
                    assertThat(pos.pathIndex).isEqualTo(s.pathI)
                    assertThat(pos.verseIndex).isEqualTo(s.verseI)
                    verifyOnce(bibleRepo).getSingle(s.book, s.chapter, s.verse)
                } else {
                    assertThat(pos.pathIndex == s.pathI && pos.verseIndex == s.verseI).isFalse()
                    verifyZeroInteractions(bibleRepo)
                }
                clearInvocations(bibleRepo)
            }
        }
    }

    private data class Selection(
        val pathI: Int,
        val verseI: Int,
        val isValid: Boolean = false,
        val book: String = "",
        val chapter: Int = 0,
        val verse: Int = 0
    )

    private fun scores(vararg values: Int): List<Score> {
        return values.map { Score(it, 2) }
    }
}
