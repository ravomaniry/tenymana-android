package mg.maniry.tenymana.ui.game

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import mg.maniry.tenymana.gameLogic.models.Puzzle
import mg.maniry.tenymana.gameLogic.shared.puzzleBuilder.PuzzleBuilder
import mg.maniry.tenymana.gameLogic.shared.session.*
import mg.maniry.tenymana.repositories.BibleRepo
import mg.maniry.tenymana.repositories.GameRepo
import mg.maniry.tenymana.repositories.UserRepo
import mg.maniry.tenymana.repositories.models.Session
import mg.maniry.tenymana.repositories.models.User
import mg.maniry.tenymana.ui.app.AppViewModel
import mg.maniry.tenymana.ui.app.Screen
import mg.maniry.tenymana.utils.KDispatchers
import mg.maniry.tenymana.utils.newViewModelFactory

interface GameViewModel {
    val sessions: LiveData<List<Session>>
    val screen: LiveData<Screen>
    val session: LiveData<Session?>
    val puzzle: LiveData<Puzzle?>
    val onSessionClick: (Session) -> Unit
    val position: SessionPosition?

    fun onPuzzleCompleted()
    fun saveAndContinue()
    fun onPathSelected(pathIndex: Int, verseIndex: Int?)
    fun closePathDetails()
    fun openCompletedPathDetails()
    fun refreshData()
}

class GameViewModelImpl(
    appViewModel: AppViewModel,
    private val userRepo: UserRepo,
    private val gameRepo: GameRepo,
    private val bibleRepo: BibleRepo,
    private val puzzleBuilder: PuzzleBuilder,
    private val dispatchers: KDispatchers
) : ViewModel(), GameViewModel {
    private var afterPathDetails = Screen.PUZZLE
    override val sessions = gameRepo.sessions
    var shouldNavigate = false
    override val screen = appViewModel.screen
    override var session = MutableLiveData<Session?>(null)

    override val puzzle = MutableLiveData<Puzzle?>(null)
    override var position: SessionPosition? = null

    private var shouldRefreshData = false

    private val userObserver = Observer<User?> {
        if (it != null && shouldRefreshData) {
            viewModelScope.launch(dispatchers.main) {
                gameRepo.initialize(it.id)
                shouldRefreshData = false
            }
        }
    }

    override val onSessionClick = { item: Session ->
        session.postValue(item)
        position = item.resume()
        navigateTo(Screen.PATHS_LIST)
    }

    override fun refreshData() {
        val user = userRepo.user.value
        if (user == null) {
            shouldRefreshData = true
        } else {
            viewModelScope.launch(dispatchers.main) {
                gameRepo.initialize(user.id)
                shouldRefreshData = false
            }
        }
    }

    override fun onPathSelected(pathIndex: Int, verseIndex: Int?) {
        var canOpenVerse = verseIndex == null
        if (verseIndex == null) {
            position = session.value!!.resumePath(pathIndex)
        } else {
            canOpenVerse = session.value!!.canOpenVerse(pathIndex, verseIndex)
            if (canOpenVerse) {
                position = position!!.copy(pathIndex = pathIndex, verseIndex = verseIndex)
            }
        }
        if (canOpenVerse) {
            navigateTo(Screen.PATH_DETAILS)
            initPuzzle()
        }
        afterPathDetails = Screen.PUZZLE
    }

    override fun onPuzzleCompleted() {
        navigateTo(Screen.SOLUTION)
    }

    override fun closePathDetails() {
        navigateTo(afterPathDetails)
    }

    override fun openCompletedPathDetails() {
        saveProgress()
        navigateTo(Screen.PATH_DETAILS)
        afterPathDetails = Screen.PATHS_LIST
    }

    override fun saveAndContinue() {
        if (session.value != null && position != null && puzzle.value != null) {
            val prevPathI = position?.pathIndex
            saveProgress()
            when {
                position?.pathIndex != prevPathI -> {
                    navigateTo(Screen.PATHS_LIST)
                }
                else -> {
                    initPuzzle()
                    navigateTo(Screen.PUZZLE)
                }
            }
        }
    }

    private fun saveProgress() {
        position = session.value!!.next(position!!, puzzle.value!!)
        gameRepo.saveProgress(position!!.value.progress)
        session.postValue(position!!.value)
    }

    private fun initPuzzle() {
        puzzle.postValue(null)
        val active = session.value!!
        viewModelScope.launch(dispatchers.default) {
            val path = active.journey.paths[position!!.pathIndex]
            val verseNum = path.start + position!!.verseIndex
            val verse = bibleRepo.getSingle(path.book, path.chapter, verseNum)
            if (verse != null) {
                puzzle.postValue(puzzleBuilder.random(verse))
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

    companion object {
        fun factory(
            appViewModel: AppViewModel,
            userRepo: UserRepo,
            gameRepo: GameRepo,
            bibleRepo: BibleRepo,
            puzzleBuilder: PuzzleBuilder,
            dispatchers: KDispatchers
        ) = newViewModelFactory {
            GameViewModelImpl(
                appViewModel,
                userRepo,
                gameRepo,
                bibleRepo,
                puzzleBuilder,
                dispatchers
            )
        }
    }
}
