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
    val position: SessionPosition?

    fun continueSession()
    fun onPuzzleCompleted()
    fun saveAndContinue()
    fun onPathVerseSelect(pathIndex: Int, verseIndex: Int)
    fun closePathDetails()
}

class GameViewModelImpl(
    appViewModel: AppViewModel,
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
    override var position: SessionPosition? = null

    private val userObserver = Observer<User?> {
        if (it != null) {
            viewModelScope.launch(dispatchers.main) {
                gameRepo.initialize(it.id)
            }
        }
    }

    override val onSessionClick = { item: Session ->
        session.postValue(item)
        position = item.resume()
        navigateTo(Screen.PATHS_LIST)
    }

    override fun continueSession() {
        shouldNavigate = true
        navigateTo(Screen.PATH_DETAILS)
        initPuzzle()
    }

    override fun onPuzzleCompleted() {
        navigateTo(Screen.PUZZLE_SOLUTION)
    }

    override fun closePathDetails() {
        navigateTo(Screen.PUZZLE)
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
                    navigateTo(Screen.JOURNEY_COMPLETE)
                }
                position?.pathIndex != prevPathI -> navigateTo(Screen.PATHS_LIST)
                else -> {
                    initPuzzle()
                    navigateTo(Screen.PUZZLE)
                }
            }
        }
    }

    override fun onPathVerseSelect(pathIndex: Int, verseIndex: Int) {
        // check here before navigating
        //        position = SessionPosition(session.value!!, false, pathIndex, verseIndex)
        //        initPuzzle()
        //        navigateTo(Screen.PUZZLE)
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

    private fun navigateTo(dest: Screen) {
        shouldNavigate = true
        screen.postValue(dest)
    }

    init {
        userRepo.user.observeForever(userObserver)
    }

    override fun onCleared() {
        super.onCleared()
        userRepo.user.removeObserver(userObserver)
    }
}
