package mg.maniry.tenymana.ui.journeyEditor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import mg.maniry.tenymana.repositories.BibleRepo
import mg.maniry.tenymana.repositories.GameRepo
import mg.maniry.tenymana.utils.KDispatchers
import mg.maniry.tenymana.utils.newViewModelFactory

class JourneyEditorVM(
    private val gameRepo: GameRepo,
    private val bibleRepo: BibleRepo,
    private val kDispatchers: KDispatchers
) : ViewModel() {
    enum class Route {
        Summary,
        Path
    }

    private val _route = MutableLiveData(Route.Summary)
    val route: LiveData<Route> = _route
    val goToHome = MutableLiveData(false)

    val title = MutableLiveData("")
    val description = MutableLiveData("")
    val pathTitle = MutableLiveData("")
    val pathDescription = MutableLiveData("")
    val pathBook = MutableLiveData("")
    val pathChapter = MutableLiveData(1)
    val pathStartVerse = MutableLiveData(1)
    val pathEndVerse = MutableLiveData(2)

    private val _enableCompleteBtn = MutableLiveData(false)
    val enableCompleteBtn: LiveData<Boolean> = _enableCompleteBtn
    private val _enableSubmitPathBtn = MutableLiveData(false)
    val enableSubmitPathBtn: LiveData<Boolean> = _enableSubmitPathBtn

    fun cancelSummary() {
        goToHome.postValue(true)
    }

    fun submitSummary() {

    }

    fun onSelectPath(index: Int) {

    }

    fun onDeletePath(index: Int) {

    }

    fun onAddPath() {

    }

    fun cancelPath() {

    }

    fun submitPath() {

    }

    companion object {
        fun factory(
            gameRepo: GameRepo,
            bibleRepo: BibleRepo,
            kDispatchers: KDispatchers
        ) = newViewModelFactory {
            JourneyEditorVM(gameRepo, bibleRepo, kDispatchers)
        }
    }
}
