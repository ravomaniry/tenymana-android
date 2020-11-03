package mg.maniry.tenymana.ui.game.paths

import androidx.lifecycle.*
import mg.maniry.tenymana.R
import mg.maniry.tenymana.gameLogic.models.Score
import mg.maniry.tenymana.gameLogic.shared.session.resume
import mg.maniry.tenymana.repositories.models.Path
import mg.maniry.tenymana.repositories.models.Session
import mg.maniry.tenymana.ui.game.GameViewModel

class PathsViewModel(
    private val gameViewModel: GameViewModel
) : ViewModel() {
    private val session = gameViewModel.session
    private val collapsedLen = 40
    private val _descriptionExpanded = MutableLiveData(false)
    var descriptionExpanded: LiveData<Boolean> = _descriptionExpanded
    private val _journeyDescription = MutableLiveData("")
    val journeyDescription: LiveData<String> = _journeyDescription
    private val activePathIndex = MutableLiveData(0)

    val activePath: LiveData<Path> = Transformations.map(activePathIndex) {
        session.value!!.journey.paths[it]
    }

    val activeScores: LiveData<List<Score>?> = Transformations.map(activePathIndex) {
        session.value!!.progress.scores.getOrNull(it)
    }

    val showLeftBtn: LiveData<Boolean> = Transformations.map(activePathIndex) {
        it > 0
    }

    val showRightBtn: LiveData<Boolean> = Transformations.map(activePathIndex) {
        it < session.value!!.journey.paths.size - 1
    }

    val journeyTitle: LiveData<String> = Transformations.map(gameViewModel.session) {
        it?.journey?.title ?: ""
    }

    val descriptionBtnIcon: LiveData<Int> = Transformations.map(descriptionExpanded) {
        if (it == true) R.drawable.ic_close_fullscreen else R.drawable.ic_open_in_full
    }

    val verseClickHandler = { index: Int ->
        gameViewModel.onPathVerseSelect(activePathIndex.value!!, index)
    }

    private val updateDescription = Observer<Any?> {
        val raw = gameViewModel.session.value!!.journey.description
        val value = when {
            descriptionExpanded.value == true || raw.length < collapsedLen -> raw
            else -> "$raw ..."
        }
        _journeyDescription.postValue(value)
    }

    private val updateActivePath = Observer<Session?> {
        val pos = session.value!!.resume()
        activePathIndex.postValue(pos.pathIndex)
    }

    fun onToggleDescription() {
        _descriptionExpanded.postValue(!(descriptionExpanded.value ?: false))
    }

    fun showPrevPath() {
        if (activePathIndex.value!! > 0) {
            activePathIndex.postValue(activePathIndex.value!! - 1)
        }
    }

    fun showNextPath() {
        val current = activePathIndex.value!!
        if (current < session.value!!.journey.paths.size) {
            activePathIndex.postValue(current + 1)
        }
    }

    fun continueSession() {
        gameViewModel.continueSession()
    }

    init {
        gameViewModel.session.observeForever(updateDescription)
        gameViewModel.session.observeForever(updateActivePath)
        descriptionExpanded.observeForever(updateDescription)
    }

    override fun onCleared() {
        super.onCleared()
        gameViewModel.session.removeObserver(updateDescription)
        gameViewModel.session.removeObserver(updateActivePath)
        descriptionExpanded.removeObserver(updateDescription)
    }
}
