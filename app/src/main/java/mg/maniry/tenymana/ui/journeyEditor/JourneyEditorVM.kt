package mg.maniry.tenymana.ui.journeyEditor

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import mg.maniry.tenymana.repositories.BibleRepo
import mg.maniry.tenymana.repositories.Book
import mg.maniry.tenymana.repositories.GameRepo
import mg.maniry.tenymana.repositories.UserRepo
import mg.maniry.tenymana.repositories.models.Journey
import mg.maniry.tenymana.repositories.models.Path
import mg.maniry.tenymana.repositories.models.User
import mg.maniry.tenymana.utils.KDispatchers
import mg.maniry.tenymana.utils.newViewModelFactory

class JourneyEditorVM(
    private val userRepo: UserRepo,
    private val gameRepo: GameRepo,
    private val bibleRepo: BibleRepo,
    private val kDispatchers: KDispatchers
) : ViewModel() {
    enum class Route {
        Summary,
        Path
    }

    private val _route = MutableLiveData(Route.Summary)
    val route: LiveData<Route> = _route
    val goToHome = MutableLiveData(false)

    private var pathIndex: Int? = null
    val title = MutableLiveData("")
    val description = MutableLiveData("")
    private val _paths = MutableLiveData<List<Path>>(emptyList())
    val paths: LiveData<List<Path>> = _paths
    val pathTitle = MutableLiveData("")
    val pathDescription = MutableLiveData("")
    private val pathBook = MutableLiveData(0)
    val pathBookName = Transformations.map(pathBook) {
        if (it != null && books.size > it) books[it].name else ""
    }
    val pathChapter = MutableLiveData(1)
    val pathStartVerse = MutableLiveData(1)
    val pathEndVerse = MutableLiveData(2)
    private var books: List<Book> = emptyList()
    private var versesNum = 0
    private val _bookNames = MutableLiveData<List<String>>()
    val bookNames: LiveData<List<String>> = _bookNames

    private val userObs = Observer<User?> {
        if (it != null) {
            viewModelScope.launch(kDispatchers.main) {
                gameRepo.initialize(it.id)
            }
        }
    }

    private val _enableSubmitJourneyBtn = MutableLiveData(false)
    val enableCompleteBtn: LiveData<Boolean> = _enableSubmitJourneyBtn
    private val completeBtnObserver = Observer<Any?> {
        _enableSubmitJourneyBtn.value = !title.value.isNullOrEmpty() &&
                !_paths.value.isNullOrEmpty()
    }

    private val _enableSubmitPathBtn = MutableLiveData(false)
    val enableSubmitPathBtn: LiveData<Boolean> = _enableSubmitPathBtn
    private val submitPathBtnObs = Observer<Any?> {
        _enableSubmitPathBtn.value = !pathTitle.value.isNullOrEmpty() &&
                pathBook.value != null &&
                pathChapter.value != null &&
                pathStartVerse.value != null &&
                pathEndVerse.value != null &&
                pathStartVerse.value!! <= pathEndVerse.value!!
    }

    private val _chapters = MutableLiveData<List<String>>()
    val chapters: LiveData<List<String>> = _chapters
    private var autoUpdateValues = false
    private val bookObs = Observer<Int> { bookI ->
        if (books.isNotEmpty()) {
            _chapters.value = List(books[bookI].chapters) { (it + 1).toString() }
            if (autoUpdateValues) {
                pathChapter.value = 1
            }
        }
    }

    private val _startVerses = MutableLiveData<List<String>>()
    val startVerses: LiveData<List<String>> = _startVerses
    private val _endVerses = MutableLiveData<List<String>>()
    val endVerses: LiveData<List<String>> = _endVerses
    private val chapterObs = Observer<Int> {
        val bookI = pathBook.value
        if (books.isNotEmpty() && bookI != null) {
            viewModelScope.launch(kDispatchers.main) {
                versesNum = bibleRepo.getChapter(books[bookI].name, it).size
                if (autoUpdateValues) {
                    pathStartVerse.value = 1
                    _startVerses.value = List(versesNum) { (it + 1).toString() }
                }
            }
        }
    }
    private val startVerseObs = Observer<Int> { n ->
        _endVerses.value = List(versesNum - n + 1) { (it + n).toString() }
        val endV = pathEndVerse.value
        if (autoUpdateValues && endV != null && endV < n) {
            pathEndVerse.value = n
        }
    }

    fun cancelSummary() {
        goToHome.postValue(true)
    }

    fun submitSummary() {
        val journey = Journey("", title.value!!, description.value ?: "", paths.value!!)
        gameRepo.saveNewJourney(journey)
        goToHome.postValue(true)
    }

    fun onSelectPath(index: Int) {
        val path = paths.value?.get(index)
        if (path != null) {
            pathIndex = index
            pathTitle.value = path.name
            pathDescription.value = path.description
            pathBook.value = bookNames.value?.indexOf(path.book)
            pathStartVerse.value = path.start
            pathEndVerse.value = path.end
            _route.value = Route.Path
        }
    }

    fun onDeletePath(index: Int) {
        _paths.value = paths.value?.toMutableList()?.apply {
            removeAt(index)
        }
    }

    fun onAddPath() {
        pathTitle.postValue("")
        pathDescription.postValue("")
        _route.postValue(Route.Path)
        pathIndex = null
    }

    fun cancelPath() {
        _route.value = Route.Summary
    }

    fun submitPath() {
        val next = paths.value?.toMutableList()
        val path = Path(
            pathTitle.value!!,
            pathDescription.value ?: "",
            pathBookName.value!!,
            pathChapter.value!!,
            pathStartVerse.value!!,
            pathEndVerse.value!!
        )
        if (pathIndex == null) {
            next?.add(path)
        } else {
            next?.set(pathIndex!!, path)
        }
        _paths.value = next
        _route.value = Route.Summary
    }

    val onBookSelect: (Int) -> Unit = {
        autoUpdateValues = true
        pathBook.value = it
    }

    val onChapterSelect: (Int) -> Unit = {
        autoUpdateValues = true
        pathChapter.value = it + 1
    }

    val onStartVerseSelect: (Int) -> Unit = {
        autoUpdateValues = true
        pathStartVerse.value = it + 1
    }

    val onEndVerseSelect: (Int) -> Unit = {
        if (pathStartVerse.value != null) {
            pathEndVerse.value = pathStartVerse.value!! + it
        }
    }

    private fun initBooks() {
        viewModelScope.launch(kDispatchers.main) {
            books = bibleRepo.getBooks()
            _bookNames.value = books.map { it.name }
            onBookSelect(0)
        }
    }

    init {
        userRepo.user.observeForever(userObs)
        title.observeForever(completeBtnObserver)
        _paths.observeForever(completeBtnObserver)
        pathTitle.observeForever(submitPathBtnObs)
        pathBook.observeForever(submitPathBtnObs)
        pathChapter.observeForever(submitPathBtnObs)
        pathStartVerse.observeForever(submitPathBtnObs)
        pathEndVerse.observeForever(submitPathBtnObs)
        pathBook.observeForever(bookObs)
        pathChapter.observeForever(chapterObs)
        pathStartVerse.observeForever(startVerseObs)
        initBooks()
    }

    override fun onCleared() {
        super.onCleared()
        userRepo.user.removeObserver(userObs)
        title.removeObserver(completeBtnObserver)
        _paths.removeObserver(completeBtnObserver)
        pathTitle.removeObserver(submitPathBtnObs)
        pathBook.removeObserver(submitPathBtnObs)
        pathChapter.removeObserver(submitPathBtnObs)
        pathStartVerse.removeObserver(submitPathBtnObs)
        pathEndVerse.removeObserver(submitPathBtnObs)
        pathBook.removeObserver(bookObs)
        pathChapter.removeObserver(chapterObs)
        pathStartVerse.removeObserver(startVerseObs)
        userRepo.user.removeObserver(userObs)
    }

    companion object {
        fun factory(
            userRepo: UserRepo,
            gameRepo: GameRepo,
            bibleRepo: BibleRepo,
            kDispatchers: KDispatchers
        ) = newViewModelFactory {
            JourneyEditorVM(userRepo, gameRepo, bibleRepo, kDispatchers)
        }
    }
}
