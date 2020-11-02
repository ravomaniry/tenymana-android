package mg.maniry.tenymana.ui.game

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import mg.maniry.tenymana.gameLogic.models.Puzzle
import mg.maniry.tenymana.gameLogic.shared.puzzleBuilder.PuzzleBuilder
import mg.maniry.tenymana.gameLogic.shared.session.SessionPosition
import mg.maniry.tenymana.gameLogic.shared.session.next
import mg.maniry.tenymana.gameLogic.shared.session.resume
import mg.maniry.tenymana.repositories.BibleRepo
import mg.maniry.tenymana.repositories.GameRepo
import mg.maniry.tenymana.repositories.UserRepo
import mg.maniry.tenymana.repositories.models.Session
import mg.maniry.tenymana.repositories.models.User
import mg.maniry.tenymana.ui.app.AppViewModel
import mg.maniry.tenymana.ui.app.Screen
import mg.maniry.tenymana.utils.KDispatchers

class GameViewModel(
    private val appViewModel: AppViewModel,
    private val userRepo: UserRepo,
    private val gameRepo: GameRepo,
    private val bibleRepo: BibleRepo,
    private val puzzleBuilder: PuzzleBuilder,
    private val dispatchers: KDispatchers
) : ViewModel() {
    val sessions = gameRepo.sessions
    var shouldNavigate = false
    val screen: LiveData<Screen> = appViewModel.screen

    private val _session = MutableLiveData<Session?>(null)
    val session: LiveData<Session?> = _session

    private val _puzzle = MutableLiveData<Puzzle?>(null)
    val puzzle: LiveData<Puzzle?> = _puzzle
    private var position: SessionPosition? = null

    private val userObserver = Observer<User?> {
        if (it != null) {
            viewModelScope.launch(dispatchers.main) {
                gameRepo.initialize(it.id)
            }
        }
    }

    val onSessionClick = { item: Session ->
        shouldNavigate = true
        _session.postValue(item)
        position = item.resume()
        appViewModel.screen.postValue(Screen.PATHS_LIST)
    }

    fun continueSession() {
        shouldNavigate = true
        appViewModel.screen.postValue(Screen.PUZZLE)
        initPuzzle()
    }

    fun onPuzzleCompleted() {
        appViewModel.screen.postValue(Screen.PUZZLE_SOLUTION)
    }

    fun saveAndContinue() {
        if (session.value != null && position != null && _puzzle.value != null) {
            val prevPathI = position?.pathIndex
            position = session.value!!.next(position!!, _puzzle.value!!)
            gameRepo.saveProgress(position!!.value.progress)
            _session.postValue(position!!.value)
            shouldNavigate = true
            when {
                position?.isCompleted!! -> {
                    _puzzle.postValue(null)
                    appViewModel.screen.postValue(Screen.JOURNEY_COMPLETE)
                }
                position?.pathIndex != prevPathI -> appViewModel.screen.postValue(Screen.PATHS_LIST)
                else -> {
                    initPuzzle()
                    appViewModel.screen.postValue(Screen.PUZZLE)
                }
            }
        }
    }

    private fun initPuzzle() {
        _puzzle.postValue(null)
        val active = _session.value!!
        viewModelScope.launch(dispatchers.default) {
            val path = active.journey.paths[position!!.pathIndex]
            val verseNum = path.start + position!!.verseIndex
            val verse = bibleRepo.getSingle(path.book, path.chapter, verseNum)
            if (verse != null) {
                _puzzle.postValue(puzzleBuilder.linkClear(verse))
            }
        }
    }

    init {
        userRepo.user.observeForever(userObserver)
    }

    override fun onCleared() {
        super.onCleared()
        userRepo.user.removeObserver(userObserver)
    }
}
