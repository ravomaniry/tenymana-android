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

    private val _score = MutableLiveData<String>()
    val score: LiveData<String> = _score

    val displayVerse = Transformations.map(gameViewModel.puzzle) {
        "${it?.verse?.book} ${it?.verse?.chapter}:${it?.verse?.verse}"
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
                syncScore()
            }
        }
    }

    private fun triggerReRender() {
        invalidate.postValue(true)
    }

    private fun syncScore() {
        val totalScore = gameViewModel.session.value!!.progress.totalScore
        _score.postValue((puzzle.value!!.score + totalScore).toString())
    }

}
