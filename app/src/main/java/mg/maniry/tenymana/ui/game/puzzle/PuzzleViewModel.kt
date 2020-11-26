package mg.maniry.tenymana.ui.game.puzzle

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import mg.maniry.tenymana.gameLogic.linkClear.LinkClearPuzzle
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

    fun onComplete() {
        gameViewModel.onPuzzleCompleted()
    }

    fun canUseBonusOne(): Boolean {
        val totalScore = gameViewModel.session.value?.progress?.totalScore ?: 0
        val puzzleScore = puzzle.value?.score?.value ?: 0
        return totalScore + puzzleScore >= bonusOnePrice
    }

    companion object {
        const val bonusOnePrice = 5

        fun factory(gameViewModel: GameViewModel) = newViewModelFactory {
            PuzzleViewModel(gameViewModel)
        }
    }
}
