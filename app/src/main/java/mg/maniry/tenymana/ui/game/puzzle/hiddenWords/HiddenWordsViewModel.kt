package mg.maniry.tenymana.ui.game.puzzle.hiddenWords

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import mg.maniry.tenymana.gameLogic.hiddenWords.HiddenWordsGroup
import mg.maniry.tenymana.gameLogic.hiddenWords.HiddenWordsPuzzle
import mg.maniry.tenymana.gameLogic.models.Character
import mg.maniry.tenymana.gameLogic.models.Puzzle
import mg.maniry.tenymana.gameLogic.models.Word
import mg.maniry.tenymana.ui.game.puzzle.PuzzleViewModel
import mg.maniry.tenymana.ui.views.hiddenWords.SelectHandler
import mg.maniry.tenymana.utils.newViewModelFactory

class HiddenWordsViewModel(
    private val puzzleViewModel: PuzzleViewModel
) : ViewModel() {
    private val _puzzle = MutableLiveData<HiddenWordsPuzzle?>()
    private val _groups = MutableLiveData<List<HiddenWordsGroup>?>()
    val groups: LiveData<List<HiddenWordsGroup>?> = _groups

    val colors = puzzleViewModel.colors

    private val _words = MutableLiveData<List<Word>?>()
    val words: LiveData<List<Word>?> = _words

    private val _characters: MutableList<MutableList<Character?>> = mutableListOf()
    private val _charactersMLD = MutableLiveData<List<List<Character?>>>()
    val characters: LiveData<List<List<Character?>>> = _charactersMLD

    private val puzzleObs = Observer<Puzzle?> {
        if (it is HiddenWordsPuzzle) {
            _puzzle.value = it
            _words.postValue(it.verse.words)
            _groups.postValue(it.groups.toList())
        }
    }

    fun cancel(index: Int) {

    }

    fun propose(index: Int) {

    }

    val onSelect: SelectHandler = { gIndex, cIndex ->

    }

    private fun syncLiveData() {
        _charactersMLD.postValue(_characters.toList())
    }

    init {
        puzzleViewModel.puzzle.observeForever(puzzleObs)
    }

    override fun onCleared() {
        super.onCleared()
        puzzleViewModel.puzzle.removeObserver(puzzleObs)
    }

    companion object {
        fun factory(puzzleViewModel: PuzzleViewModel) = newViewModelFactory {
            HiddenWordsViewModel(puzzleViewModel)
        }
    }
}
