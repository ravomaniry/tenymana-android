package mg.maniry.tenymana.ui.app

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mg.maniry.tenymana.repositories.BibleRepo
import mg.maniry.tenymana.repositories.UserRepo
import mg.maniry.tenymana.utils.newViewModelFactory

enum class Screen {
    HOME,
    GAMES_LIST,
    PATHS_LIST,
    PATH_DETAILS,
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

    companion object {
        fun factory(bibleRepo: BibleRepo, userRepo: UserRepo) = newViewModelFactory {
            AppViewModel(bibleRepo, userRepo)
        }
    }
}
