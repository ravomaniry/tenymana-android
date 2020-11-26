package mg.maniry.tenymana.ui.game.puzzle.linkClear

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mg.maniry.tenymana.gameLogic.linkClear.LinkClearPuzzle
import mg.maniry.tenymana.gameLogic.models.*
import mg.maniry.tenymana.ui.game.puzzle.PuzzleViewModel
import mg.maniry.tenymana.ui.views.charsGrid.ProposeFn
import mg.maniry.tenymana.utils.KDispatchers
import mg.maniry.tenymana.utils.newViewModelFactory

class LinkClearViewModel(
    private val puzzleViewModel: PuzzleViewModel,
    private val kDispatchers: KDispatchers
) : ViewModel() {
    private var lockPropose = false
    private var prevPuzzle: Puzzle? = null
    private val puzzle: LinkClearPuzzle? get() = puzzleViewModel.puzzle.value as LinkClearPuzzle?

    val colors = puzzleViewModel.colors

    private val _words = MutableLiveData<List<Word>>()
    val words: LiveData<List<Word>> = _words

    private val _grid = MutableLiveData<Grid<Character>?>()
    val grid: LiveData<Grid<Character>?> = _grid
    private val _prevGrid = MutableLiveData<Grid<Character>?>()
    val prevGrid: LiveData<Grid<Character>?> = _prevGrid

    private val _highlighted = MutableLiveData<List<Point>?>()
    val highlighted: LiveData<List<Point>?> = _highlighted

    private val _diffs = MutableLiveData<List<Move>?>()
    val diffs: LiveData<List<Move>?> = _diffs

    private val inGameAnimDuration = 500.0
    private val helpAnimDuration = 300L
    private val _animDuration = MutableLiveData<Double>()
    val animDuration: LiveData<Double> = _animDuration

    private val _propose = MutableLiveData<ProposeFn?>()
    val propose: LiveData<ProposeFn?> = _propose

    private val puzzleObserver = Observer<Puzzle?> {
        if (it != null && prevPuzzle == null && it is LinkClearPuzzle) {
            _propose.postValue(null)
            _words.postValue(it.verse.words)
            _animDuration.postValue(helpAnimDuration.toDouble())
            viewModelScope.launch(kDispatchers.default) {
                for (i in (it.solution.size - 1).downTo(0)) {
                    _grid.postValue(it.solution[i].grid)
                    _prevGrid.postValue(it.solution[i].grid)
                    kDispatchers.delay(helpAnimDuration)
                    _highlighted.postValue(it.solution[i].points)
                    kDispatchers.delay(helpAnimDuration)
                }
                withContext(kDispatchers.main) {
                    _grid.postValue(it.grid)
                    _propose.postValue(this@LinkClearViewModel::propose)
                    _prevGrid.postValue(it.prevGrid)
                    _animDuration.postValue(inGameAnimDuration)
                    _highlighted.postValue(null)
                }
            }

        }
        prevPuzzle = it
    }

    private val _invalidate = MutableLiveData(false)
    val invalidate: LiveData<Boolean> = _invalidate

    fun onUpdateDone() {
        _invalidate.postValue(false)
    }

    fun propose(move: Move) {
        if (!lockPropose) {
            lockPropose = true
            viewModelScope.launch(kDispatchers.main) {
                val didUpdate = withContext(kDispatchers.default) {
                    puzzle?.propose(move)
                }
                if (didUpdate == true) {
                    if (puzzle?.completed == true) {
                        puzzleViewModel.onComplete()
                    } else {
                        triggerReRender(true)
                    }
                }
                lockPropose = false
            }
        }
    }

    private fun triggerReRender(animate: Boolean) {
        viewModelScope.launch(kDispatchers.default) {
            if (animate) {
                _highlighted.postValue(puzzle?.cleared)
                kDispatchers.delay(inGameAnimDuration.toLong())
                _diffs.postValue(puzzle?.diffs)
            }
            _prevGrid.postValue(puzzle?.prevGrid)
            _invalidate.postValue(true)
        }
    }

    fun undo() {
        val didUpdate = puzzle?.undo()
        if (didUpdate == true) {
            triggerReRender(false)
            _highlighted.postValue(null)
            _diffs.postValue(null)
        }
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
    }

    companion object {
        fun factory(puzzleViewModel: PuzzleViewModel, kDispatchers: KDispatchers) =
            newViewModelFactory { LinkClearViewModel(puzzleViewModel, kDispatchers) }
    }
}
