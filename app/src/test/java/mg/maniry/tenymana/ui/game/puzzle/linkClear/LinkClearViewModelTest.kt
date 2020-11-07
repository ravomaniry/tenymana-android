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
import mg.maniry.tenymana.gameLogic.models.Grid
import mg.maniry.tenymana.gameLogic.models.Point
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
        val solution = listOf(
            SolutionItem(Grid(listOf(chars('A', 'B'))), listOf(Point(0, 0), Point(1, 0))),
            SolutionItem(Grid(listOf(chars('A', 'B', 'C', 'D'))), listOf(Point(2, 0), Point(3, 0)))
        )
        val puzzle: LinkClearPuzzle = mock {
            on { this.solution } doReturn solution
            on { this.grid } doReturn solution.last().grid
            on { this.cleared } doReturn MutableLiveData<List<Point>?>(null)
        }
        val grids = mutableListOf<Grid<*>?>()
        val highlights = mutableListOf<List<Point>?>()
        val proposes = mutableListOf<ProposeFn?>()
        val animDrations = mutableListOf<Double>()
        viewModel.grid.observeForever { grids.add(it) }
        viewModel.highlight.observeForever { highlights.add(it) }
        viewModel.propose.observeForever { proposes.add(it) }
        viewModel.animDuration.observeForever { animDrations.add(it) }
        runBlocking { puzzleCont.postValue(puzzle) }
        assertThat(grids).isEqualTo(listOf(solution[1].grid, solution[0].grid, solution[1].grid))
        assertThat(highlights).isEqualTo(listOf(solution[1].points, solution[0].points, null))
        assertThat(proposes).isEqualTo(listOf(null, puzzleViewModel::propose))
        assertThat(animDrations).isEqualTo(listOf(300.0, 500.0))
        // Animation is done
        // - Puzzle grid & grid.cleared are displayed
        (puzzle.cleared as MutableLiveData).postValue(listOf(Point(2, 2), Point(2, 3)))
        assertThat(viewModel.grid.value).isEqualTo(puzzle.grid)
        assertThat(viewModel.highlight.value).isEqualTo(listOf(Point(2, 2), Point(2, 3)))
    }
}
