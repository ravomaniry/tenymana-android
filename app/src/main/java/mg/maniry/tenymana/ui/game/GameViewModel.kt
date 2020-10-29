package mg.maniry.tenymana.ui.game

import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mg.maniry.tenymana.gameLogic.linkClear.LinkClearPuzzle
import mg.maniry.tenymana.gameLogic.models.Puzzle
import mg.maniry.tenymana.gameLogic.shared.session.resume
import mg.maniry.tenymana.repositories.BibleRepo
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
    private val bibleRepo: BibleRepo,
    private val random: Random
) : ViewModel() {
    val sessions = gameRepo.sessions
    var shouldNavigate = false
    val screen: LiveData<Screen> = appViewModel.screen

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

    // Hardcoded to resume for now
    private fun initPuzzle() {
        _puzzle.postValue(null)
        val active = _session.value!!
        val next = active.resume()
        viewModelScope.launch {
            val path = active.journey.paths[next.pathIndex]
            val verseNum = path.start + next.verseIndex
            val verse = bibleRepo.getSingle(path.book, path.chapter, verseNum)
            if (verse != null) {
                withContext(Dispatchers.Default) {
                    _puzzle.postValue(LinkClearPuzzle.build(verse, random))
                }
            }
        }
    }

    fun resumeSession() {
        shouldNavigate = true
        appViewModel.screen.postValue(Screen.PUZZLE)
        initPuzzle()
    }

    fun onPuzzleCompleted() {
        // go to solution screen
    }

    fun goToNextVerse() {
        // save progress
        // if journey is completed
        //      - go to congrats screen
        // if path is completed
        //      - go to paths screen
        // else load next verse and go to puzzle screen
    }

    init {
        userRepo.user.observeForever(userObserver)
    }

    override fun onCleared() {
        super.onCleared()
        userRepo.user.removeObserver(userObserver)
    }
}
