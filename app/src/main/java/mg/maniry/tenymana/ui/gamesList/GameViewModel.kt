package mg.maniry.tenymana.ui.gamesList

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import mg.maniry.tenymana.repositories.GameRepo
import mg.maniry.tenymana.repositories.UserRepo
import mg.maniry.tenymana.repositories.models.Session
import mg.maniry.tenymana.repositories.models.User
import mg.maniry.tenymana.ui.app.AppViewModel
import mg.maniry.tenymana.ui.app.Screen

class GameViewModel(
    private val appViewModel: AppViewModel,
    private val userRepo: UserRepo,
    private val gameRepo: GameRepo
) : ViewModel() {
    val sessions = gameRepo.sessions
    val screen: LiveData<Screen> = appViewModel.screen

    private val _session = MutableLiveData<Session?>(null)
    val session: LiveData<Session?> = _session

    var shouldNavigate = false

    private val userObserver = Observer<User?> {
        if (it != null) {
            viewModelScope.launch {
                gameRepo.initialize(it.id)
            }
        }
    }

    val onSessionClick = { item: Session ->
        shouldNavigate = true
        _session.postValue(item)
        appViewModel.screen.postValue(Screen.PATHS_LIST)
    }

    fun onResumeSession() {
        shouldNavigate = true
        appViewModel.screen.postValue(Screen.PUZZLE)
    }

    init {
        userRepo.user.observeForever(userObserver)
    }

    override fun onCleared() {
        super.onCleared()
        userRepo.user.removeObserver(userObserver)
    }
}
