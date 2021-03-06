package mg.maniry.tenymana.ui.game.puzzle.header

import androidx.lifecycle.*
import mg.maniry.tenymana.gameLogic.anagram.AnagramPuzzle
import mg.maniry.tenymana.gameLogic.hiddenWords.HiddenWordsPuzzle
import mg.maniry.tenymana.gameLogic.linkClear.LinkClearPuzzle
import mg.maniry.tenymana.gameLogic.models.Puzzle
import mg.maniry.tenymana.ui.app.Screen
import mg.maniry.tenymana.ui.game.GameViewModel
import mg.maniry.tenymana.ui.game.puzzle.PuzzleViewModel
import mg.maniry.tenymana.utils.newViewModelFactory

class PuzzleHeaderViewModel(
    private val gameViewModel: GameViewModel,
    puzzleViewModel: PuzzleViewModel
) : ViewModel() {
    val colors = puzzleViewModel.colors
    private val puzzle = gameViewModel.puzzle
    private var removeScoreObserver: (() -> Unit)? = null

    private val _score = MutableLiveData<String>()
    val score: LiveData<String> = _score

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

    fun onHelp() {
        gameViewModel.helpScreen.value = when (puzzle.value) {
            is LinkClearPuzzle -> Screen.LINK_CLEAR_HELP
            is HiddenWordsPuzzle -> Screen.HIDDEN_WORDS_HELP
            is AnagramPuzzle -> Screen.ANAGRAM_HELP
            else -> null
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
        fun factory(gameViewModel: GameViewModel, puzzleViewModel: PuzzleViewModel) =
            newViewModelFactory {
                PuzzleHeaderViewModel(gameViewModel, puzzleViewModel)
            }
    }
}
