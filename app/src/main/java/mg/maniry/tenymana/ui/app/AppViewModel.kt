package mg.maniry.tenymana.ui.app

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mg.maniry.tenymana.repositories.BibleRepo
import mg.maniry.tenymana.repositories.UserRepo

enum class Screen {
    HOME,
    GAMES_LIST,
    PATHS_LIST,
    PUZZLE,
    PUZZLE_SOLUTION,
    JOURNEY_COMPLETE
}

class AppViewModel(
    private val bibleRepo: BibleRepo,
    private val userRepo: UserRepo
) : ViewModel() {
    val screen = MutableLiveData<Screen>(Screen.HOME)
    private val _isReady = MutableLiveData(false)
    val isReady: LiveData<Boolean> = _isReady

    init {
        viewModelScope.launch {
            userRepo.setup()
            bibleRepo.setup()
            _isReady.postValue(true)
        }
    }
}
