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
