package mg.maniry.tenymana.ui.game.puzzle

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mg.maniry.tenymana.gameLogic.linkClear.LinkClearPuzzle
import mg.maniry.tenymana.gameLogic.models.Move
import mg.maniry.tenymana.gameLogic.models.Word
import mg.maniry.tenymana.ui.game.GameViewModel
import mg.maniry.tenymana.ui.game.colors.DefaultColors
import mg.maniry.tenymana.ui.game.colors.GameColors
import mg.maniry.tenymana.ui.game.colors.LinkClearColors
import mg.maniry.tenymana.utils.KDispatchers
import mg.maniry.tenymana.utils.newViewModelFactory

class PuzzleViewModel(
    private val gameViewModel: GameViewModel,
    private val kDispatchers: KDispatchers
) : ViewModel() {
    private var lockPropose = false
    val puzzle = gameViewModel.puzzle
    val colors: LiveData<GameColors> = Transformations.map(gameViewModel.puzzle) {
        when (it) {
            is LinkClearPuzzle -> LinkClearColors()
            else -> DefaultColors()
        }
    }

    val words: LiveData<List<Word>?> = Transformations.map(gameViewModel.puzzle) {
        it?.verse?.words
    }

    val invalidate = MutableLiveData(false)

    fun propose(move: Move) {
        if (!lockPropose) {
            lockPropose = true
            viewModelScope.launch(kDispatchers.main) {
                val didUpdate = withContext(kDispatchers.default) { puzzle.value?.propose(move) }
                if (didUpdate == true) {
                    if (puzzle.value?.completed == true) {
                        gameViewModel.onPuzzleCompleted()
                    } else {
                        triggerReRender()
                    }
                }
                lockPropose = false
            }
        }
    }

    fun undo() {
        val didUpdate = puzzle.value?.undo()
        if (didUpdate == true) {
            triggerReRender()
        }
    }

    private fun triggerReRender() {
        invalidate.postValue(true)
    }

    companion object {
        fun factory(gameViewModel: GameViewModel, kDispatchers: KDispatchers) =
            newViewModelFactory { PuzzleViewModel(gameViewModel, kDispatchers) }
    }
}
