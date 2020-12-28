package mg.maniry.tenymana.ui.game.puzzle.hiddenWords

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import mg.maniry.tenymana.gameLogic.hiddenWords.HiddenWordsPuzzle
import mg.maniry.tenymana.gameLogic.models.Puzzle
import mg.maniry.tenymana.gameLogic.models.Word
import mg.maniry.tenymana.ui.game.puzzle.PuzzleViewModel
import mg.maniry.tenymana.utils.KDispatchers
import mg.maniry.tenymana.utils.newViewModelFactory

class HiddenWordsViewModel(
    private val puzzleViewModel: PuzzleViewModel,
    private val kDispatchers: KDispatchers
) : ViewModel() {
    private val _puzzle = MutableLiveData<HiddenWordsPuzzle?>()
    val puzzle: LiveData<HiddenWordsPuzzle?> = _puzzle

    val colors = puzzleViewModel.colors

    private val _words = MutableLiveData<List<Word>?>()
    val words: LiveData<List<Word>?> = _words

    private val puzzleObs = Observer<Puzzle?> {
        if (it is HiddenWordsPuzzle) {
            _puzzle.value = it
            _words.postValue(it.verse.words)
        }
    }

    init {
        puzzleViewModel.puzzle.observeForever(puzzleObs)
    }

    override fun onCleared() {
        super.onCleared()
        puzzleViewModel.puzzle.removeObserver(puzzleObs)
    }

    companion object {
        fun factory(puzzleViewModel: PuzzleViewModel, kDispatchers: KDispatchers) =
            newViewModelFactory { HiddenWordsViewModel(puzzleViewModel, kDispatchers) }
    }
}
