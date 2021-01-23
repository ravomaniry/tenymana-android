package mg.maniry.tenymana.ui.game.puzzle.linkClear

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import mg.maniry.tenymana.gameLogic.linkClear.LinkClearPuzzle
import mg.maniry.tenymana.gameLogic.linkClear.SolutionItem
import mg.maniry.tenymana.gameLogic.models.*
import mg.maniry.tenymana.ui.game.colors.DefaultColors
import mg.maniry.tenymana.ui.game.colors.GameColors
import mg.maniry.tenymana.ui.game.puzzle.PuzzleViewModel
import mg.maniry.tenymana.ui.views.charsGrid.ProposeFn
import mg.maniry.tenymana.utils.TestDispatchers
import mg.maniry.tenymana.utils.chars
import mg.maniry.tenymana.utils.verifyNever
import mg.maniry.tenymana.utils.verifyOnce
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
            on { this.canUseBonusOne() } doReturn true
        }
        val viewModel = LinkClearViewModel(puzzleViewModel, TestDispatchers)
        assertThat(viewModel.grid.value).isNull()
        // Puzzle changes triggers animations
        val solution = listOf(
            SolutionItem(Grid(listOf(chars('A', 'B'))), listOf(Point(0, 0), Point(1, 0))),
            SolutionItem(Grid(listOf(chars('A', 'B', 'C', 'D'))), listOf(Point(2, 0), Point(3, 0)))
        )
        val verse = BibleVerse.fromText("", 1, 1, "Abc def")
        val prevGrid = solution.last().grid.toMutable()
        val puzzle: LinkClearPuzzle = mock {
            on { this.solution } doReturn solution
            on { this.grid } doReturn solution.last().grid
            on { this.prevGrid } doReturn prevGrid
            on { this.verse } doReturn verse
        }
        // Values during & after animation
        val grids = mutableListOf<Grid<*>?>()
        val prevGrids = mutableListOf<Grid<*>?>()
        val highlights = mutableListOf<List<Point>?>()
        val proposes = mutableListOf<ProposeFn?>()
        val animDrations = mutableListOf<Double>()
        viewModel.grid.observeForever { grids.add(it) }
        viewModel.highlighted.observeForever { highlights.add(it) }
        viewModel.propose.observeForever { proposes.add(it) }
        viewModel.animDuration.observeForever { animDrations.add(it) }
        viewModel.prevGrid.observeForever { prevGrids.add(it) }
        runBlocking {
            // before puzzle: no animation
            viewModel.onMounted?.invoke()
            assertThat(grids).isEmpty()
            // post puzzle: no mount
            puzzleCont.postValue(puzzle)
            assertThat(grids).isEmpty()
            // mount
            viewModel.onMounted?.invoke()
            assertThat(grids).isEqualTo(
                listOf(solution[1].grid, solution[0].grid, solution[1].grid)
            )
            assertThat(prevGrids).isEqualTo(
                listOf(solution[1].grid, solution[0].grid, solution[1].grid.toMutable())
            )
            assertThat(highlights).isEqualTo(listOf(solution[1].points, solution[0].points, null))
            assertThat(animDrations).isEqualTo(listOf(300.0, 500.0))
            assertThat(proposes.size).isEqualTo(2)
            assertThat(proposes[0]).isNull()
            assertThat(proposes[1]).isNotNull()
            assertThat(viewModel.grid.value).isEqualTo(puzzle.grid)
            // onMount does nothing
            assertThat(viewModel.onMounted).isNull()
        }
    }

    @Test
    fun proposeAndComplete() {
        val words = BibleVerse.fromText("Maio", 1, 1, "Abc de").words.toMutableList()
        val verse = BibleVerse("Maio", 1, 1, "Abc de", words)
        val score = MutableLiveData(0)
        var proposeResult = false
        var isComplete = false
        var undoResult = false
        var cleared: List<Point>? = null
        var diffs: List<Move>? = null
        var hintBonusResult: List<Point>? = null
        var prevGrid: Grid<Character> = mock()
        val puzzle: LinkClearPuzzle = mock {
            on { this.verse } doAnswer { verse }
            on { this.score } doAnswer { score }
            on { this.completed } doAnswer { isComplete }
            on { this.cleared } doAnswer { cleared }
            on { this.diffs } doAnswer { diffs }
            on { this.prevGrid } doAnswer { prevGrid }
            on { undo() } doAnswer { undoResult }
            on { propose(any()) } doAnswer { proposeResult }
            on { useBonusHintOne(any()) } doAnswer { hintBonusResult }
            on { useBonusRevealChars(1, PuzzleViewModel.bonusOnePrice) } doReturn true
        }
        val puzzleLD: LiveData<Puzzle?> = MutableLiveData(puzzle)
        val colors: LiveData<GameColors> = MutableLiveData(DefaultColors())
        var canUseBonusOne = false
        val puzzleViewModel: PuzzleViewModel = mock {
            on { this.puzzle } doReturn puzzleLD
            on { this.colors } doReturn colors
            on { this.canUseBonusOne() } doAnswer { canUseBonusOne }
        }
        val viewModel = LinkClearViewModel(puzzleViewModel, TestDispatchers)
        val highlights = mutableListOf<List<Point>?>()
        val diffsHistory = mutableListOf<List<Move>?>()
        viewModel.highlighted.observeForever { highlights.add(it) }
        viewModel.diffs.observeForever { diffsHistory.add(it) }
        runBlocking {
            viewModel.onMounted!!()
            val invalidate = viewModel.invalidate as MutableLiveData<Boolean>
            // Wrong response
            proposeResult = false
            viewModel.propose(Move.xy(0, 0, 1, 0))
            assertThat(viewModel.invalidate.value).isFalse()
            assertThat(highlights).isEqualTo(listOf(null))
            assertThat(diffsHistory).isEmpty()
            // true + set animation values to null after animation
            highlights.removeAll { true }
            prevGrid = mock()
            proposeResult = true
            diffs = emptyList()
            cleared = emptyList()
            score.postValue(10)
            viewModel.propose(Move.xy(0, 0, 2, 0))
            assertThat(viewModel.invalidate.value).isTrue()
            assertThat(highlights).isEqualTo(listOf(emptyList<Point>(), null))
            assertThat(diffsHistory).isEqualTo(listOf(emptyList<Point>(), null))
            assertThat(viewModel.prevGrid.value).isEqualTo(prevGrid)
            invalidate.postValue(false)
            // UNDO
            val prevHighlighs = viewModel.highlighted.value
            val prevDiffs = viewModel.diffs.value
            // -> no change
            undoResult = false
            viewModel.undo()
            assertThat(viewModel.invalidate.value).isFalse()
            assertThat(viewModel.highlighted.value).isEqualTo(prevHighlighs)
            assertThat(viewModel.diffs.value).isEqualTo(prevDiffs)
            assertThat(viewModel.prevGrid.value).isEqualTo(prevGrid)
            // -> chanced
            prevGrid = mock()
            undoResult = true
            viewModel.undo()
            assertThat(viewModel.invalidate.value).isTrue()
            assertThat(viewModel.highlighted.value).isNull()
            assertThat(viewModel.diffs.value).isNull()
            assertThat(viewModel.prevGrid.value).isEqualTo(prevGrid)
            invalidate.postValue(false)
            // Bonus: can use
            canUseBonusOne = true
            hintBonusResult = listOf(Point(1, 1))
            viewModel.useHintBonusOne()
            verifyOnce(puzzle).useBonusHintOne(PuzzleViewModel.bonusOnePrice)
            clearInvocations(puzzle)
            assertThat(viewModel.highlighted.value).isEqualTo(hintBonusResult)
            // can not use bonus
            hintBonusResult = null
            invalidate.postValue(false)
            canUseBonusOne = false
            viewModel.useHintBonusOne()
            verifyZeroInteractions(puzzle)
            assertThat(invalidate.value).isFalse()
            assertThat(viewModel.highlighted.value).isEqualTo(listOf(Point(1, 1)))
            // Bonus reveal
            canUseBonusOne = true
            invalidate.postValue(false)
            viewModel.useRevealOneBonus()
            assertThat(viewModel.invalidate.value).isTrue()
            // Complete
            verifyNever(puzzleViewModel).onComplete()
            isComplete = true
            score.postValue(20)
            viewModel.propose(Move.xy(0, 0, 1, 0))
            verifyOnce(puzzleViewModel).onComplete()
        }
    }

    @Test
    fun replacePuzzle() {
        val verse = BibleVerse.fromText("", 1, 1, "Abc def")
        val puzzleCont = MutableLiveData<Puzzle>()
        val puzzleViewModel: PuzzleViewModel = mock {
            on { this.puzzle } doReturn puzzleCont
            on { this.canUseBonusOne() } doReturn true
        }
        val viewModel = LinkClearViewModel(puzzleViewModel, TestDispatchers)
        assertThat(viewModel.grid.value).isNull()
        val grids = mutableListOf<Grid<*>?>()
        viewModel.grid.observeForever { grids.add(it) }
        runBlocking {
            // first puzzle -> animate
            val solution0 = SolutionItem(
                Grid(listOf(chars('A', 'B'))),
                listOf(Point(0, 0), Point(1, 0))
            )
            puzzleCont.postValue(puzzle(listOf(solution0), verse))
            viewModel.onMounted!!()
            assertThat(grids).isEqualTo(listOf(solution0.grid, solution0.grid))
            // animate again when puzzle changes
            grids.removeAll { true }
            val solution1 = SolutionItem(
                Grid(listOf(chars('A', 'B', 'C', 'D'))),
                listOf(Point(2, 0), Point(3, 0))
            )
            puzzleCont.postValue(puzzle(listOf(solution1), verse))
            viewModel.onMounted!!()
            assertThat(grids).isEqualTo(listOf(solution1.grid, solution1.grid))
        }
    }

    private fun puzzle(
        solution: List<SolutionItem<Character>>,
        verse: BibleVerse
    ): LinkClearPuzzle {
        return mock {
            on { this.solution } doReturn solution
            on { this.grid } doReturn solution.last().grid
            on { this.prevGrid } doReturn solution.last().grid.toMutable()
            on { this.verse } doReturn verse
        }
    }
}
