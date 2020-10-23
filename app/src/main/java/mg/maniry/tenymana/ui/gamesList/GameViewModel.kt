package mg.maniry.tenymana.ui.gamesList

import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mg.maniry.tenymana.gameLogic.linkClear.LinkClearPuzzle
import mg.maniry.tenymana.gameLogic.models.BibleVerse
import mg.maniry.tenymana.gameLogic.models.Puzzle
import mg.maniry.tenymana.repositories.GameRepo
import mg.maniry.tenymana.repositories.UserRepo
import mg.maniry.tenymana.repositories.models.Session
import mg.maniry.tenymana.repositories.models.User
import mg.maniry.tenymana.ui.app.AppViewModel
import mg.maniry.tenymana.ui.app.Screen
import mg.maniry.tenymana.utils.Random

class GameViewModel(
    private val appViewModel: AppViewModel,
    private val userRepo: UserRepo,
    private val gameRepo: GameRepo,
    private val random: Random
) : ViewModel() {
    val sessions = gameRepo.sessions
    var shouldNavigate = false
    val screen: LiveData<Screen> = appViewModel.screen
    private var verse: BibleVerse? = null

    private val _session = MutableLiveData<Session?>(null)
    val session: LiveData<Session?> = _session

    private val _puzzle = MutableLiveData<Puzzle?>(null)
    val puzzle: LiveData<Puzzle?> = _puzzle

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

    private fun initBoard() {
        _puzzle.postValue(null)
        if (verse != null) {
            viewModelScope.launch {
                withContext(Dispatchers.Default) {
                    _puzzle.postValue(LinkClearPuzzle.build(verse!!, random))
                }
            }
        }
    }

    fun onResumeSession() {
        shouldNavigate = true
        appViewModel.screen.postValue(Screen.PUZZLE)
        initBoard()
    }

    init {
        userRepo.user.observeForever(userObserver)
    }

    override fun onCleared() {
        super.onCleared()
        userRepo.user.removeObserver(userObserver)
    }
}
