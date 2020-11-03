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

interface GameViewModel {
    val sessions: LiveData<List<Session>>
    val screen: LiveData<Screen>
    val session: LiveData<Session?>
    val puzzle: LiveData<Puzzle?>
    val onSessionClick: (Session) -> Unit

    fun continueSession()
    fun onPuzzleCompleted()
    fun saveAndContinue()
}

class GameViewModelImpl(
    private val appViewModel: AppViewModel,
    private val userRepo: UserRepo,
    private val gameRepo: GameRepo,
    private val bibleRepo: BibleRepo,
    private val puzzleBuilder: PuzzleBuilder,
    private val dispatchers: KDispatchers
) : ViewModel(), GameViewModel {
    override val sessions = gameRepo.sessions
    var shouldNavigate = false
    override val screen = appViewModel.screen
    override var session = MutableLiveData<Session?>(null)

    override val puzzle = MutableLiveData<Puzzle?>(null)
    private var position: SessionPosition? = null

    private val userObserver = Observer<User?> {
        if (it != null) {
            viewModelScope.launch(dispatchers.main) {
                gameRepo.initialize(it.id)
            }
        }
    }

    override val onSessionClick = { item: Session ->
        shouldNavigate = true
        session.postValue(item)
        position = item.resume()
        appViewModel.screen.postValue(Screen.PATHS_LIST)
    }

    override fun continueSession() {
        shouldNavigate = true
        appViewModel.screen.postValue(Screen.PUZZLE)
        initPuzzle()
    }

    override fun onPuzzleCompleted() {
        appViewModel.screen.postValue(Screen.PUZZLE_SOLUTION)
    }

    override fun saveAndContinue() {
        if (session.value != null && position != null && puzzle.value != null) {
            val prevPathI = position?.pathIndex
            position = session.value!!.next(position!!, puzzle.value!!)
            gameRepo.saveProgress(position!!.value.progress)
            session.postValue(position!!.value)
            shouldNavigate = true
            when {
                position?.isCompleted!! -> {
                    puzzle.postValue(null)
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
        puzzle.postValue(null)
        val active = session.value!!
        viewModelScope.launch(dispatchers.default) {
            val path = active.journey.paths[position!!.pathIndex]
            val verseNum = path.start + position!!.verseIndex
            val verse = bibleRepo.getSingle(path.book, path.chapter, verseNum)
            if (verse != null) {
                puzzle.postValue(puzzleBuilder.linkClear(verse))
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
