package mg.maniry.tenymana.ui.game.puzzle.linkClear

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import mg.maniry.tenymana.gameLogic.linkClear.LinkClearPuzzle
import mg.maniry.tenymana.gameLogic.models.Character
import mg.maniry.tenymana.gameLogic.models.Grid
import mg.maniry.tenymana.gameLogic.models.Move
import mg.maniry.tenymana.gameLogic.models.Puzzle
import mg.maniry.tenymana.ui.game.puzzle.PuzzleViewModel
import mg.maniry.tenymana.ui.game.puzzle.views.ProposeFn
import mg.maniry.tenymana.utils.KDispatchers
import mg.maniry.tenymana.utils.newViewModelFactory

class LinkClearViewModel(
    private val puzzleViewModel: PuzzleViewModel,
    private val kDispatchers: KDispatchers
) : ViewModel() {
    private val animDuration = 500L
    private var prevPuzzle: Puzzle? = null

    val invalidate = puzzleViewModel.invalidate
    val colors = puzzleViewModel.colors
    val words = puzzleViewModel.words

    private val _grid = MutableLiveData<Grid<Character>?>()
    val grid: LiveData<Grid<Character>?> = _grid

    private val _move = MutableLiveData<Move>()
    val move: LiveData<Move> = _move

    private val _propose = MutableLiveData<ProposeFn?>()
    val propose: LiveData<ProposeFn?> = _propose

    private val puzzleObserver = Observer<Puzzle?> {
        if (it != null && prevPuzzle == null && it is LinkClearPuzzle) {
            _propose.postValue(null)
            viewModelScope.launch(kDispatchers.default) {
                for (i in (it.solution.size - 1).downTo(0)) {
                    _grid.postValue(it.solution[i].grid)
                    kDispatchers.delay(animDuration)
                    _move.postValue(it.solution[i].move)
                    kDispatchers.delay(animDuration)
                }
                _grid.postValue(it.grid)
                _propose.postValue(puzzleViewModel::propose)
            }
        }
        prevPuzzle = it
    }

    fun undo() {
        puzzleViewModel.undo()
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
