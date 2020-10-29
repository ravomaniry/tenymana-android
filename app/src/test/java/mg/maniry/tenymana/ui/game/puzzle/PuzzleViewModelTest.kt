package mg.maniry.tenymana.ui.game.puzzle

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import mg.maniry.tenymana.gameLogic.linkClear.LinkClearPuzzle
import mg.maniry.tenymana.gameLogic.models.BibleVerse
import mg.maniry.tenymana.gameLogic.models.Grid
import mg.maniry.tenymana.gameLogic.models.Move
import mg.maniry.tenymana.gameLogic.models.Puzzle
import mg.maniry.tenymana.repositories.models.Journey
import mg.maniry.tenymana.repositories.models.Path
import mg.maniry.tenymana.repositories.models.Progress
import mg.maniry.tenymana.repositories.models.Session
import mg.maniry.tenymana.ui.game.GameViewModel
import mg.maniry.tenymana.utils.addresses
import org.junit.Rule
import org.junit.Test

class PuzzleViewModelTest {
    @get:Rule
    val liveDataRule = InstantTaskExecutorRule()

    @Test
    fun gamePlay() {
        val grid = Grid(
            listOf(
                addresses(0, 0, 0, 1, 0, 2),
                addresses(2, 0, 2, 1, null, null)
            )
        )
        val verse = BibleVerse.fromText("Maio", 1, 1, "Abc de")
        val puzzle: Puzzle = LinkClearPuzzle(grid, verse)
        val puzzleLD = MutableLiveData(puzzle)
        val session = Session(
            Journey.empty("0").copy(paths = listOf(Path("path0", "...", "Matio", 1, 1, 10))),
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
        viewModel.propose(Move.xy(0, 0, 2, 0))
        assertThat(viewModel.invalidate.value).isTrue()
    }
}
