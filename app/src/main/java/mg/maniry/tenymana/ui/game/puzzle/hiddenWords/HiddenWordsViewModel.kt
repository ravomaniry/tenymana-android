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
import mg.maniry.tenymana.utils.newViewModelFactory

class HiddenWordsViewModel(
    private val puzzleViewModel: PuzzleViewModel
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

    private val _activeGroupIndex = MutableLiveData<Int>()
    private val activeGroupIndex: LiveData<Int> = _activeGroupIndex
    private val _activeGroup = MutableLiveData<HiddenWordsGroup>()
    val activeGroup: LiveData<HiddenWordsGroup> = _activeGroup

    private var groupIndexObs = Observer<Any?> {
        syncActiveGroup()
    }

    private val puzzleObs = Observer<Puzzle?> {
        if (it is HiddenWordsPuzzle) {
            puzzle.postValue(it)
            _words.postValue(it.verse.words)
            groups = it.groups
            resetSelections()
            syncCharacters()
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
        selected.removeAll { true }
        _activeGroupIndex.postValue(index)
        syncCharacters()
    }

    fun propose() {
        val index = _activeGroupIndex.value
        val puzzleValue = puzzle.value
        if (selected.isNotEmpty() && index != null && puzzleValue != null) {
            val success = puzzleValue.propose(index, selected.toList())
            if (success) {
                groups = puzzleValue.groups
                if (groups[index].resolved) {
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
        val group = groups[_activeGroupIndex.value ?: 0]
        _activeGroup.postValue(group)
    }

    private fun getActiveChars(): List<Character?>? {
        val gIndex = activeGroupIndex.value ?: return null
        return groups[gIndex].chars
    }

    private fun checkAndComplete() {
        if (puzzle.value?.completed == true) {
            puzzleViewModel.onComplete()
        }
    }

    private fun activateUncompletedGroup() {
        _activeGroupIndex.postValue(puzzle.value?.firstGroup)
    }

    init {
        puzzleViewModel.puzzle.observeForever(puzzleObs)
        _activeGroupIndex.observeForever(groupIndexObs)
    }

    override fun onCleared() {
        super.onCleared()
        puzzleViewModel.puzzle.removeObserver(puzzleObs)
        _activeGroupIndex.removeObserver(groupIndexObs)
    }

    companion object {
        fun factory(puzzleViewModel: PuzzleViewModel) = newViewModelFactory {
            HiddenWordsViewModel(puzzleViewModel)
        }
    }
}
