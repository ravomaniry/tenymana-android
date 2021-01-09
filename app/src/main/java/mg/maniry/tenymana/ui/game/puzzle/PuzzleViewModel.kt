package mg.maniry.tenymana.ui.game.puzzle

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import mg.maniry.tenymana.gameLogic.anagram.AnagramPuzzle
import mg.maniry.tenymana.gameLogic.hiddenWords.HiddenWordsPuzzle
import mg.maniry.tenymana.gameLogic.linkClear.LinkClearPuzzle
import mg.maniry.tenymana.ui.game.GameViewModel
import mg.maniry.tenymana.ui.game.colors.*
import mg.maniry.tenymana.utils.newViewModelFactory

class PuzzleViewModel(
    private val gameViewModel: GameViewModel
) : ViewModel() {
    val puzzle = gameViewModel.puzzle

    val colors: LiveData<GameColors> = Transformations.map(gameViewModel.puzzle) {
        when (it) {
            is LinkClearPuzzle -> LinkClearColors()
            is HiddenWordsPuzzle -> HiddenWordsColors()
            is AnagramPuzzle -> AnagramColors()
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
