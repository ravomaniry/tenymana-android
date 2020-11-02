package mg.maniry.tenymana.ui.game.puzzle

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import mg.maniry.tenymana.gameLogic.models.BibleVerse
import mg.maniry.tenymana.gameLogic.models.Move
import mg.maniry.tenymana.gameLogic.models.Puzzle
import mg.maniry.tenymana.repositories.models.Journey
import mg.maniry.tenymana.repositories.models.Path
import mg.maniry.tenymana.repositories.models.Progress
import mg.maniry.tenymana.repositories.models.Session
import mg.maniry.tenymana.ui.game.GameViewModel
import mg.maniry.tenymana.utils.verifyNever
import mg.maniry.tenymana.utils.verifyOnce
import org.junit.Rule
import org.junit.Test

class PuzzleViewModelTest {
    @get:Rule
    val liveDataRule = InstantTaskExecutorRule()

    @Test
    fun gamePlay() {
        val verse = BibleVerse.fromText("Maio", 1, 1, "Abc de")
        var score = 0
        var proposeResult = false
        var isComplete = false
        val puzzle: Puzzle = mock {
            on { this.verse } doReturn verse
            on { this.score } doAnswer { score }
            on { this.completed } doAnswer { isComplete }
            on { propose(any()) } doAnswer { proposeResult }
        }
        val puzzleLD = MutableLiveData(puzzle)
        val session = Session(
            Journey("0", paths = listOf(Path("path0", "...", "Matio", 1, 1, 10))),
            Progress("0", 0, emptyList(), emptyList())
        )
        val sessionLD = MutableLiveData(session)
        val gameVm: GameViewModel = mock {
            on { this.puzzle } doReturn puzzleLD
            on { this.session } doReturn sessionLD
        }
        val viewModel = PuzzleViewModel(gameVm)
        // Wrong response
        viewModel.propose(Move.xy(0, 0, 1, 0))
        assertThat(viewModel.invalidate.value).isFalse()
        // true
        proposeResult = true
        score = 10
        viewModel.propose(Move.xy(0, 0, 2, 0))
        assertThat(viewModel.invalidate.value).isTrue()
        assertThat(viewModel.score.value).isEqualTo("10")
        // Bonus tests here ...
        // End
        verifyNever(gameVm).onPuzzleCompleted()
        // complete
        isComplete = true
        score = 20
        viewModel.propose(Move.xy(0, 0, 1, 0))
        verifyOnce(gameVm).onPuzzleCompleted()
    }
}
