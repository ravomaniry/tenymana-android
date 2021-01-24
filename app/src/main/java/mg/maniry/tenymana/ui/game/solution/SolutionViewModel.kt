package mg.maniry.tenymana.ui.game.solution

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import mg.maniry.tenymana.gameLogic.models.BibleVerse
import mg.maniry.tenymana.gameLogic.models.Puzzle
import mg.maniry.tenymana.gameLogic.shared.bibleVerse.calcStars
import mg.maniry.tenymana.repositories.BibleRepo
import mg.maniry.tenymana.ui.game.GameViewModel
import mg.maniry.tenymana.utils.Memo
import mg.maniry.tenymana.utils.newViewModelFactory

class SolutionViewModel(
    private val gameViewModel: GameViewModel,
    private val bibleRepo: BibleRepo
) : ViewModel() {
    private val puzzle = gameViewModel.puzzle
    private val _verses = MutableLiveData<List<BibleVerse>>()
    val verses: LiveData<List<BibleVerse>> get() = _verses
    private val _showBigView = MutableLiveData(false)
    val showBigView: LiveData<Boolean> = _showBigView
    private val _showExpandBtn = MutableLiveData(false)
    val showExpandBtn: LiveData<Boolean> = _showExpandBtn

    private val _title = MutableLiveData("")
    val title: LiveData<String> = _title
    val singleVerseText = Transformations.map(gameViewModel.puzzle) {
        it?.verse?.text ?: ""
    }

    private val scoreMemo = Memo(
        { listOf(puzzle.value?.score?.value) },
        { puzzle.value?.score?.value ?: 0 }
    )
    private val starsMemo = Memo(
        { listOf(puzzle.value?.verse, puzzle.value?.score) },
        { buildStarsList(puzzle.value?.verse, puzzle.value?.score?.value) }
    )

    val score: String get() = scoreMemo.value.toString()
    val stars: List<Boolean> = starsMemo.value

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
        if (gameViewModel.activePathIsCompleted()) {
            gameViewModel.openCompletedPathDetails()
        } else {
            gameViewModel.saveAndContinue()
        }
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

    fun autoExpand() {
        if (gameViewModel.activePathIsCompleted()) {
            showPreviousVerses()
        }
    }

    private fun buildStarsList(verse: BibleVerse?, score: Int?): List<Boolean> {
        if (verse != null && score != null) {
            val stars = verse.calcStars(score)
            return listOf(stars >= 1, stars >= 2, stars >= 3)
        }
        return listOf(false, false, false)
    }

    override fun onCleared() {
        super.onCleared()
        gameViewModel.puzzle.removeObserver(reset)
        gameViewModel.puzzle.removeObserver(updateTitle)
        gameViewModel.puzzle.removeObserver(expandBtnObserver)
        _showBigView.removeObserver(updateTitle)
    }

    init {
        gameViewModel.puzzle.observeForever(reset)
        gameViewModel.puzzle.observeForever(updateTitle)
        gameViewModel.puzzle.observeForever(expandBtnObserver)
        _showBigView.observeForever(updateTitle)
    }

    companion object {
        fun factory(gameViewModel: GameViewModel, bibleRepo: BibleRepo) = newViewModelFactory {
            SolutionViewModel(gameViewModel, bibleRepo)
        }
    }
}

private fun GameViewModel.activePathIsCompleted(): Boolean {
    val pathI = position?.pathIndex ?: return false
    val verseI = position?.verseIndex ?: return false
    val path = session.value?.journey?.paths?.get(pathI) ?: return false
    return verseI == path.size - 1
}
