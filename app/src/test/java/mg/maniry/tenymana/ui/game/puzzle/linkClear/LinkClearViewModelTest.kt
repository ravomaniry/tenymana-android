package mg.maniry.tenymana.ui.game.puzzle.linkClear

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import mg.maniry.tenymana.gameLogic.linkClear.LinkClearPuzzle
import mg.maniry.tenymana.gameLogic.linkClear.SolutionItem
import mg.maniry.tenymana.gameLogic.models.Character
import mg.maniry.tenymana.gameLogic.models.Grid
import mg.maniry.tenymana.gameLogic.models.Move
import mg.maniry.tenymana.gameLogic.models.Puzzle
import mg.maniry.tenymana.ui.game.puzzle.PuzzleViewModel
import mg.maniry.tenymana.ui.game.puzzle.views.ProposeFn
import mg.maniry.tenymana.utils.TestDispatchers
import mg.maniry.tenymana.utils.chars
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class LinkClearViewModelTest {
    @get:Rule
    val liveDataRule = InstantTaskExecutorRule()

    @Test
    fun animationSimulation() {
        val puzzleCont = MutableLiveData<Puzzle>()
        val puzzleViewModel: PuzzleViewModel = mock {
            on { this.puzzle } doReturn puzzleCont
        }
        val viewModel = LinkClearViewModel(puzzleViewModel, TestDispatchers)
        assertThat(viewModel.grid.value).isNull()
        // Puzzle changes triggers animations
        val solution = listOf<SolutionItem<Character>>(
            SolutionItem(Grid(listOf(chars('A', 'B'))), Move.xy(0, 0, 1, 0)),
            SolutionItem(Grid(listOf(chars('A', 'B', 'C', 'D'))), Move.xy(2, 0, 3, 0))
        )
        val puzzle: LinkClearPuzzle = mock {
            on { this.solution } doReturn solution
            on { this.grid } doReturn solution.last().grid
        }
        val grids = mutableListOf<Grid<*>>()
        val moves = mutableListOf<Move>()
        val proposes = mutableListOf<ProposeFn?>()
        viewModel.grid.observeForever { grids.add(it) }
        viewModel.move.observeForever { moves.add(it) }
        viewModel.propose.observeForever { proposes.add(it) }
        runBlocking { puzzleCont.postValue(puzzle) }
        assertThat(viewModel.grid.value).isEqualTo(puzzle.grid)
        assertThat(grids).isEqualTo(listOf(solution[1].grid, solution[0].grid, solution[1].grid))
        assertThat(moves).isEqualTo(listOf(solution[1].move, solution[0].move))
        assertThat(proposes).isEqualTo(listOf(null, puzzleViewModel::propose))
    }
}
