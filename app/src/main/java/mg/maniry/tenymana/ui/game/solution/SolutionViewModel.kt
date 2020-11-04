package mg.maniry.tenymana.ui.game.solution

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import mg.maniry.tenymana.gameLogic.models.BibleVerse
import mg.maniry.tenymana.gameLogic.models.Puzzle
import mg.maniry.tenymana.repositories.BibleRepo
import mg.maniry.tenymana.ui.game.GameViewModel

class SolutionViewModel(
    private val gameViewModel: GameViewModel,
    private val bibleRepo: BibleRepo
) : ViewModel() {
    companion object {
        fun factory(gameViewModel: GameViewModel, bibleRepo: BibleRepo): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    @Suppress("unchecked_cast")
                    return SolutionViewModel(gameViewModel, bibleRepo) as T
                }
            }
        }
    }

    private val _verses = MutableLiveData<List<BibleVerse>>()
    val verses: LiveData<List<BibleVerse>> get() = _verses
    private val _showBigView = MutableLiveData(false)
    val showBigView: LiveData<Boolean> = _showBigView
    private val _showExpandBtn = MutableLiveData(false)

    private val _title = MutableLiveData("")
    val title: LiveData<String> = _title
    val singleVerseText = Transformations.map(gameViewModel.puzzle) {
        it?.verse?.text ?: ""
    }

    private val reset = Observer<Puzzle?> {
        _showBigView.postValue(false)
    }

    private val updateTitle = Observer<Any?> {
        val verse = gameViewModel.puzzle.value?.verse
        if (verse != null) {
            if (_showBigView.value == true) {
                _title.postValue("${verse.book} ${verse.chapter}")
            } else {
                _title.postValue("${verse.book} ${verse.chapter}:${verse.verse}")
            }
        }
    }

    private val expandBtnObserver = Observer<Any?> {
        _showExpandBtn.postValue(gameViewModel.position!!.verseIndex > 0)
    }

    fun saveAndContinue() {
        gameViewModel.saveAndContinue()
    }

    fun showPreviousVerses() {
        val pos = gameViewModel.position!!
        val path = gameViewModel.session.value!!.journey.paths[pos.pathIndex]
        val minV = path.start
        val maxV = path.start + pos.verseIndex
        _showBigView.postValue(true)
        viewModelScope.launch {
            val values = bibleRepo.get(path.book, path.chapter, minV, maxV)
            _verses.postValue(values)
        }
    }

    init {
        gameViewModel.puzzle.observeForever(reset)
        gameViewModel.puzzle.observeForever(updateTitle)
        gameViewModel.puzzle.observeForever(expandBtnObserver)
        _showBigView.observeForever(updateTitle)
    }

    override fun onCleared() {
        super.onCleared()
        gameViewModel.puzzle.removeObserver(reset)
        gameViewModel.puzzle.removeObserver(updateTitle)
        gameViewModel.puzzle.removeObserver(expandBtnObserver)
        _showBigView.removeObserver(updateTitle)
    }
}
