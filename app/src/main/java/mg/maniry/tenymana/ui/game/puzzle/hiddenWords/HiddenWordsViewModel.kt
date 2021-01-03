package mg.maniry.tenymana.ui.game.puzzle.hiddenWords

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import mg.maniry.tenymana.gameLogic.hiddenWords.HiddenWordsGroup
import mg.maniry.tenymana.gameLogic.hiddenWords.HiddenWordsPuzzle
import mg.maniry.tenymana.gameLogic.models.Character
import mg.maniry.tenymana.gameLogic.models.Puzzle
import mg.maniry.tenymana.gameLogic.models.Word
import mg.maniry.tenymana.ui.game.puzzle.PuzzleViewModel
import mg.maniry.tenymana.utils.KDispatchers
import mg.maniry.tenymana.utils.newViewModelFactory

class HiddenWordsViewModel(
    private val puzzleViewModel: PuzzleViewModel,
    private val kDispatchers: KDispatchers
) : ViewModel() {
    private val puzzle = MutableLiveData<HiddenWordsPuzzle?>()
    private var groups: List<HiddenWordsGroup> = emptyList()

    val colors = puzzleViewModel.colors

    private val _words = MutableLiveData<List<Word>?>()
    val words: LiveData<List<Word>?> = _words

    private var selected = mutableListOf<Int>()

    private val _proposition = MutableLiveData("")
    val proposition: LiveData<String> = _proposition

    private var _characters = MutableLiveData<List<Character?>>()
    val characters: LiveData<List<Character?>> = _characters

    private var _activeGroupIndex = 0
    private val activeGroupIndexLD = MutableLiveData<Int>()
    private val _activeGroup = MutableLiveData<HiddenWordsGroup>()
    val activeGroup: LiveData<HiddenWordsGroup> = _activeGroup

    private val puzzleObs = Observer<Puzzle?> {
        if (it is HiddenWordsPuzzle) {
            puzzle.postValue(it)
            _words.postValue(it.verse.words)
            groups = it.groups
            onActiveGroupChange(0)
        }
    }

    fun cancel() {
        if (selected.isNotEmpty()) {
            selected.removeAll { true }
            syncCharacters()
        }
    }

    fun onActiveGroupChange(index: Int) {
        _activeGroupIndex = index
        syncActiveGroup()
        resetSelections()
        syncCharacters()
    }

    fun propose() {
        val puzzleValue = puzzle.value
        if (selected.isNotEmpty() && puzzleValue != null) {
            val success = puzzleValue.propose(_activeGroupIndex, selected.toList())
            if (success) {
                groups = puzzleValue.groups
                _words.postValue(puzzleValue.verse.words)
                if (groups[_activeGroupIndex].resolved) {
                    activateUncompletedGroup()
                }
                checkAndComplete()
                syncActiveGroup()
                resetSelections()
                syncCharacters()
            } else {
                cancel()
            }
        }
    }

    fun onCharSelect(index: Int) {
        val chars = _characters.value
        if (chars != null) {
            if (chars.size > index && chars[index] != null && !selected.contains(index)) {
                selected.add(index)
                syncCharacters()
            }
        }
    }

    private fun resetSelections() {
        selected.removeAll { true }
    }

    private fun syncCharacters() {
        val allChars = getActiveChars() ?: return
        val next = allChars.toMutableList()
        var nextProp = ""
        for (i in selected) {
            val char = allChars[i]
            if (char != null) {
                nextProp += char.value
            }
            next[i] = null
        }
        _characters.postValue(next)
        _proposition.postValue(nextProp)
    }

    private fun syncActiveGroup() {
        activeGroupIndexLD.postValue(_activeGroupIndex)
        _activeGroup.postValue(groups[_activeGroupIndex])
    }

    private fun getActiveChars(): List<Character?>? {
        return groups[_activeGroupIndex].chars
    }

    private fun checkAndComplete() {
        if (puzzle.value?.completed == true) {
            puzzleViewModel.onComplete()
        }
    }

    private fun activateUncompletedGroup() {
        syncActiveGroup()
        val prevIndex = _activeGroupIndex
        viewModelScope.launch(kDispatchers.main) {
            kDispatchers.delay(600)
            if (prevIndex == _activeGroupIndex) {
                onActiveGroupChange(puzzle.value?.firstGroup ?: 0)
            }
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
