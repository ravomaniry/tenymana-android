package mg.maniry.tenymana.ui.game.puzzle.linkClear

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mg.maniry.tenymana.gameLogic.linkClear.LinkClearPuzzle
import mg.maniry.tenymana.gameLogic.models.Character
import mg.maniry.tenymana.gameLogic.models.Grid
import mg.maniry.tenymana.gameLogic.models.Point
import mg.maniry.tenymana.gameLogic.models.Puzzle
import mg.maniry.tenymana.ui.game.puzzle.PuzzleViewModel
import mg.maniry.tenymana.ui.game.puzzle.views.ProposeFn
import mg.maniry.tenymana.utils.KDispatchers
import mg.maniry.tenymana.utils.newViewModelFactory

class LinkClearViewModel(
    private val puzzleViewModel: PuzzleViewModel,
    private val kDispatchers: KDispatchers
) : ViewModel() {
    private var prevPuzzle: Puzzle? = null
    private val puzzle: LinkClearPuzzle? get() = puzzleViewModel.puzzle.value as LinkClearPuzzle?

    val invalidate = puzzleViewModel.invalidate
    val colors = puzzleViewModel.colors
    val words = puzzleViewModel.words

    private val _grid = MutableLiveData<Grid<Character>?>()
    val grid: LiveData<Grid<Character>?> = _grid

    private val _highlighted = MutableLiveData<List<Point>?>()
    val highlighted: LiveData<List<Point>?> = _highlighted

    private val inGameAnimDuration = 500.0
    private val helpAnimDuration = 300L
    private val _animDuration = MutableLiveData<Double>()
    val animDuration: LiveData<Double> = _animDuration

    private val _propose = MutableLiveData<ProposeFn?>()
    val propose: LiveData<ProposeFn?> = _propose

    private val clearedObserver = Observer<List<Point>?> { _highlighted.postValue(it) }
    private var removeClearObserver = {}

    private val puzzleObserver = Observer<Puzzle?> {
        removeClearObserver()
        if (it != null && prevPuzzle == null && it is LinkClearPuzzle) {
            _propose.postValue(null)
            _animDuration.postValue(helpAnimDuration.toDouble())
            viewModelScope.launch(kDispatchers.default) {
                for (i in (it.solution.size - 1).downTo(0)) {
                    _grid.postValue(it.solution[i].grid)
                    kDispatchers.delay(helpAnimDuration)
                    _highlighted.postValue(it.solution[i].points)
                    kDispatchers.delay(helpAnimDuration)
                }
                withContext(kDispatchers.main) {
                    _grid.postValue(it.grid)
                    _propose.postValue(puzzleViewModel::propose)
                    _animDuration.postValue(inGameAnimDuration)
                    it.cleared.observeForever(clearedObserver)
                    removeClearObserver = { it.cleared.removeObserver(clearedObserver) }
                }
            }
        }
        prevPuzzle = it
    }

    fun undo() {
        puzzleViewModel.undo()
    }

    fun useBonusOne() {
        if (puzzleViewModel.canUseBonusOne()) {
            _highlighted.postValue(puzzle?.useBonusOne(PuzzleViewModel.bonusOnePrice))
        }
    }

    init {
        puzzleViewModel.puzzle.observeForever(puzzleObserver)
    }

    override fun onCleared() {
        super.onCleared()
        puzzleViewModel.puzzle.removeObserver(puzzleObserver)
        removeClearObserver()
    }

    companion object {
        fun factory(puzzleViewModel: PuzzleViewModel, kDispatchers: KDispatchers) =
            newViewModelFactory { LinkClearViewModel(puzzleViewModel, kDispatchers) }
    }
}
