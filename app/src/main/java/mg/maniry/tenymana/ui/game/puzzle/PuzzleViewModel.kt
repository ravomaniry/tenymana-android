package mg.maniry.tenymana.ui.game.puzzle

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import mg.maniry.tenymana.gameLogic.linkClear.LinkClearPuzzle
import mg.maniry.tenymana.gameLogic.models.Move
import mg.maniry.tenymana.gameLogic.models.Word
import mg.maniry.tenymana.ui.game.GameViewModel
import mg.maniry.tenymana.ui.game.colors.DefaultColors
import mg.maniry.tenymana.ui.game.colors.GameColors
import mg.maniry.tenymana.ui.game.colors.LinkClearColors
import mg.maniry.tenymana.utils.newViewModelFactory

class PuzzleViewModel(
    private val gameViewModel: GameViewModel
) : ViewModel() {
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
        val didUpdate = puzzle.value?.propose(move)
        if (didUpdate == true) {
            if (puzzle.value?.completed == true) {
                gameViewModel.onPuzzleCompleted()
            } else {
                triggerReRender()
            }
        }
    }

    private fun triggerReRender() {
        invalidate.postValue(true)
    }

    companion object {
        fun factory(gameViewModel: GameViewModel) = newViewModelFactory {
            PuzzleViewModel(gameViewModel)
        }
    }
}
