package mg.maniry.tenymana.ui.game.puzzle.header

import androidx.lifecycle.*
import mg.maniry.tenymana.gameLogic.linkClear.LinkClearPuzzle
import mg.maniry.tenymana.gameLogic.models.Puzzle
import mg.maniry.tenymana.ui.game.GameViewModel
import mg.maniry.tenymana.ui.game.colors.DefaultColors
import mg.maniry.tenymana.ui.game.colors.GameColors
import mg.maniry.tenymana.ui.game.colors.LinkClearColors
import mg.maniry.tenymana.utils.newViewModelFactory

class PuzzleHeaderViewModel(
    private val gameViewModel: GameViewModel
) : ViewModel() {
    private val puzzle = gameViewModel.puzzle
    private var removeScoreObserver: (() -> Unit)? = null

    private val _score = MutableLiveData<String>()
    val score: LiveData<String> = _score

    val colors: LiveData<GameColors> = Transformations.map(gameViewModel.puzzle) {
        when (it) {
            is LinkClearPuzzle -> LinkClearColors()
            else -> DefaultColors()
        }
    }

    val displayVerse = Transformations.map(gameViewModel.puzzle) {
        if (it == null) "" else "${it.verse.book} ${it.verse.chapter}:${it.verse.verse}"
    }

    private val syncScore = Observer<Int?> {
        val totalScore = gameViewModel.session.value!!.progress.totalScore + (it ?: 0)
        _score.postValue(totalScore.toString())
    }

    private val puzzleObserver = Observer<Puzzle?> {
        if (it == null) {
            removeScoreObserver?.invoke()
        } else {
            removeScoreObserver = { it.score.removeObserver(syncScore) }
            it.score.observeForever(syncScore)
        }
    }

    override fun onCleared() {
        super.onCleared()
        removeScoreObserver?.invoke()
        puzzle.removeObserver(puzzleObserver)
    }

    init {
        puzzle.observeForever(puzzleObserver)
    }

    companion object {
        fun factory(gameViewModel: GameViewModel) = newViewModelFactory {
            PuzzleHeaderViewModel(gameViewModel)
        }
    }
}
