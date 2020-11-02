package mg.maniry.tenymana.ui.app

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mg.maniry.tenymana.repositories.BibleRepo

enum class Screen {
    HOME,
    GAMES_LIST,
    PATHS_LIST,
    PUZZLE,
    PUZZLE_SOLUTION,
    JOURNEY_COMPLETE
}

class AppViewModel(
    private val bibleRepo: BibleRepo
) : ViewModel() {
    val screen = MutableLiveData<Screen>(Screen.HOME)

    init {
        viewModelScope.launch {
            bibleRepo.setup()
        }
    }
}
