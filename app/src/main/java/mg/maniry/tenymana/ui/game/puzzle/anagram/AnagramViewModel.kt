package mg.maniry.tenymana.ui.game.puzzle.anagram

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import mg.maniry.tenymana.gameLogic.anagram.AnagramPuzzle
import mg.maniry.tenymana.gameLogic.models.Character
import mg.maniry.tenymana.gameLogic.models.Puzzle
import mg.maniry.tenymana.gameLogic.models.Word
import mg.maniry.tenymana.ui.game.puzzle.PuzzleViewModel
import mg.maniry.tenymana.utils.newViewModelFactory

class AnagramViewModel(
    private val puzzleViewModel: PuzzleViewModel
) : ViewModel() {
    val colors = puzzleViewModel.colors
    private var puzzle: AnagramPuzzle? = null

    private var _proposition = ""
    private val _propositionLD = MutableLiveData("")
    val proposition: LiveData<String> = _propositionLD

    private val _words = MutableLiveData<List<Word>>()
    val words: LiveData<List<Word>> = _words

    private var indexes = mutableListOf<Int>()
    private var _characters = mutableListOf<Character?>()
    private val _charactersLD = MutableLiveData<List<Character?>>()
    val characters: LiveData<List<Character?>> = _charactersLD

    private val _animate = MutableLiveData(true)
    val animate: LiveData<Boolean> = _animate

    private val puzzleObs = Observer<Puzzle?> {
        if (it is AnagramPuzzle) {
            puzzle = it
            _words.postValue(it.verse.words)
            _characters = it.chars.toMutableList()
            syncChars()
        }
    }

    fun onCharSelect(index: Int) {
        if (_characters[index] != null) {
            _proposition += _characters[index]?.value
            _characters[index] = null
            indexes.add(index)
            syncChars()
        }
    }

    fun cancel() {
        val chars = puzzle?.chars
        indexes = mutableListOf()
        if (chars != null) {
            _proposition = ""
            _characters = chars.toMutableList()
            syncChars()
        }
    }

    fun propose() {
        val resolved = puzzle?.propose(indexes)
        if (resolved == true) {
            if (puzzle!!.completed) {
                puzzleViewModel.onComplete()
            } else {
                indexes = mutableListOf()
                _proposition = ""
                _characters = puzzle!!.chars.toMutableList()
                _words.postValue(puzzle!!.verse.words)
                _animate.postValue(true)
                syncChars()
            }
        } else {
            cancel()
        }
    }

    fun useBonusOne() {
        if (puzzleViewModel.canUseBonusOne()) {
            puzzle?.useBonus(1, PuzzleViewModel.bonusOnePrice)
            _words.postValue(puzzle!!.verse.words)
        }
    }

    fun onAnimationDone() {
        _animate.postValue(false)
    }

    private fun syncChars() {
        _charactersLD.postValue(_characters.toList())
        _propositionLD.postValue(_proposition)
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
            AnagramViewModel(puzzleViewModel)
        }
    }
}
