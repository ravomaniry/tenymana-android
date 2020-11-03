package mg.maniry.tenymana.ui.game.paths

import androidx.lifecycle.*
import mg.maniry.tenymana.R
import mg.maniry.tenymana.ui.game.GameViewModel

class PathsViewModel(
    private val gameViewModel: GameViewModel
) : ViewModel() {
    private val collapsedLen = 40
    private val descriptionExpanded = MutableLiveData(false)
    private val _journeyDescription = MutableLiveData("")
    val journeyDescription: LiveData<String> = _journeyDescription

    val journeyTitle: LiveData<String> = Transformations.map(gameViewModel.session) {
        it?.journey?.title ?: ""
    }

    val descriptionBtnIcon: LiveData<Int> = Transformations.map(descriptionExpanded) {
        if (it == true) R.drawable.ic_close_fullscreen else R.drawable.ic_open_in_full
    }

    private val updateDescription = Observer<Any?> {
        val raw = gameViewModel.session.value!!.journey.description
        val value = when {
            descriptionExpanded.value == true || raw.length < collapsedLen -> raw
            else -> "$raw ..."
        }
        _journeyDescription.postValue(value)
    }

    fun onToggleDescription() {
        descriptionExpanded.postValue(!(descriptionExpanded.value ?: false))
    }

    fun continueSession() {
        gameViewModel.continueSession()
    }

    init {
        gameViewModel.session.observeForever(updateDescription)
        descriptionExpanded.observeForever(updateDescription)
    }

    override fun onCleared() {
        super.onCleared()
        gameViewModel.session.removeObserver(updateDescription)
        descriptionExpanded.removeObserver(updateDescription)
    }
}
