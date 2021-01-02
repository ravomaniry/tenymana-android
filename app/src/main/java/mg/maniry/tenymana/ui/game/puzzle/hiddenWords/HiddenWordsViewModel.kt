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
    private val puzzle = MutableLiveData<HiddenWordsPuzzle?>()
    private val _groups = MutableLiveData<List<HiddenWordsGroup>?>()
    val groups: LiveData<List<HiddenWordsGroup>?> = _groups

    val colors = puzzleViewModel.colors

    private val _words = MutableLiveData<List<Word>?>()
    val words: LiveData<List<Word>?> = _words

    private var selections = listOf<MutableSet<Int>>()

    private var _characters = listOf<MutableList<Character?>>()
    private val _charactersMLD = MutableLiveData<List<List<Character?>>>()
    val characters: LiveData<List<List<Character?>>> = _charactersMLD

    private val puzzleObs = Observer<Puzzle?> {
        if (it is HiddenWordsPuzzle) {
            puzzle.value = it
            _words.postValue(it.verse.words)
            _groups.postValue(it.groups.toList())
            initChars(it.groups)
        }
    }

    fun cancel() {
        selections.forEachIndexed { gI, indexes ->
            if (indexes.isNotEmpty()) {
                indexes.forEach { cI ->
                    _characters[gI][cI] = groups.value?.getOrNull(gI)?.chars?.getOrNull(cI)
                }
                indexes.removeAll { true }
            }
        }
        syncLiveData()
    }

    fun propose(index: Int) {
        val puzzleValue = puzzle.value
        if (puzzleValue != null && selections[index].isNotEmpty()) {
            val success = puzzleValue.propose(index, selections[index].toList())
            if (success) {
                _groups.postValue(puzzleValue.groups)
                checkAndComplete(puzzleValue.groups)
                resetSelections()
            } else {
                cancel()
            }
        }
    }

    val onSelect: SelectHandler = { gIndex, cIndex ->
        val selected = selections[gIndex]
        val chars = _characters[gIndex]
        if (chars.size > cIndex && chars[cIndex] != null && !selected.contains(cIndex)) {
            selected.add(cIndex)
            _characters[gIndex][cIndex] = null
        }
    }

    private fun resetSelections() {
        selections.forEach { it.removeAll { true } }
    }

    private fun initChars(groups: List<HiddenWordsGroup>) {
        selections = groups.map { mutableSetOf<Int>() }
        _characters = groups.map { it.chars.toMutableList() }
        syncLiveData()
    }

    private fun syncLiveData() {
        _charactersMLD.postValue(_characters.toList())
    }

    private fun checkAndComplete(groups: List<HiddenWordsGroup>) {
        for (g in groups) {
            if (!g.resolved) {
                return
            }
        }
        puzzleViewModel.onComplete()
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
