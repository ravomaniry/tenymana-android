package mg.maniry.tenymana.ui.journeyEditor

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import mg.maniry.tenymana.repositories.BibleRepo
import mg.maniry.tenymana.repositories.Book
import mg.maniry.tenymana.repositories.GameRepo
import mg.maniry.tenymana.repositories.models.Path
import mg.maniry.tenymana.utils.KDispatchers
import mg.maniry.tenymana.utils.newViewModelFactory

class JourneyEditorVM(
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

    val title = MutableLiveData("")
    val description = MutableLiveData("")
    private val _paths = MutableLiveData<List<Path>>(emptyList())
    val paths: LiveData<List<Path>> = _paths
    val pathTitle = MutableLiveData("")
    val pathDescription = MutableLiveData("")
    private val pathBook = MutableLiveData(0)
    private val pathChapter = MutableLiveData(1)
    private val pathStartVerse = MutableLiveData(1)
    private val pathEndVerse = MutableLiveData(2)
    private var books: List<Book> = emptyList()
    private var versesNum = 0
    private val _bookNames = MutableLiveData<List<String>>()
    val bookNames: LiveData<List<String>> = _bookNames

    private val _enableCompleteBtn = MutableLiveData(false)
    val enableCompleteBtn: LiveData<Boolean> = _enableCompleteBtn
    private val completeBtnObserver = Observer<Any?> {
        _enableCompleteBtn.value = !title.value.isNullOrEmpty() && !_paths.value.isNullOrEmpty()
    }

    private val _enableSubmitPathBtn = MutableLiveData(false)
    val enableSubmitPathBtn: LiveData<Boolean> = _enableSubmitPathBtn
    private val enablePathBtnObs = Observer<Any?> {
        _enableCompleteBtn.value = !pathTitle.value.isNullOrEmpty() &&
                pathBook.value != null &&
                pathChapter.value != null &&
                pathStartVerse.value != null &&
                pathEndVerse.value != null &&
                pathStartVerse.value!! <= pathEndVerse.value!!
    }

    private val _chapters = MutableLiveData<List<String>>()
    val chapters: LiveData<List<String>> = _chapters
    private val bookObs = Observer<Int> { bookI ->
        if (books.isNotEmpty()) {
            _chapters.value = List(books[bookI].chapters) { (it + 1).toString() }
            pathChapter.value = 1
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
                pathStartVerse.value = 1
                _startVerses.value = List(versesNum) { (it + 1).toString() }
            }
        }
    }
    private val startVerseObs = Observer<Int> { n ->
        _endVerses.value = List(versesNum - n + 1) { (it + versesNum).toString() }
        val endV = pathEndVerse.value
        if (endV != null && endV < n) {
            pathEndVerse.value = n
        }
    }

    fun cancelSummary() {
        goToHome.postValue(true)
    }

    fun submitSummary() {

    }

    fun onSelectPath(index: Int) {

    }

    fun onDeletePath(index: Int) {

    }

    fun onAddPath() {
        pathTitle.postValue("")
        pathDescription.postValue("")
        _route.postValue(Route.Path)
    }

    fun cancelPath() {

    }

    fun submitPath() {

    }

    val onBookSelect: (Int) -> Unit = {
        pathBook.value = it
    }

    val onChapterSelect: (Int) -> Unit = {
        pathChapter.value = it + 1
    }

    val onStartVerseSelect: (Int) -> Unit = {
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
        title.observeForever(completeBtnObserver)
        _paths.observeForever(completeBtnObserver)
        pathTitle.observeForever(enablePathBtnObs)
        pathBook.observeForever(enablePathBtnObs)
        pathChapter.observeForever(enablePathBtnObs)
        pathStartVerse.observeForever(enablePathBtnObs)
        pathEndVerse.observeForever(enablePathBtnObs)
        pathBook.observeForever(bookObs)
        pathChapter.observeForever(chapterObs)
        pathStartVerse.observeForever(startVerseObs)
        initBooks()
    }

    override fun onCleared() {
        super.onCleared()
        title.removeObserver(completeBtnObserver)
        _paths.removeObserver(completeBtnObserver)
        pathTitle.removeObserver(enablePathBtnObs)
        pathBook.removeObserver(enablePathBtnObs)
        pathChapter.removeObserver(enablePathBtnObs)
        pathStartVerse.removeObserver(enablePathBtnObs)
        pathEndVerse.removeObserver(enablePathBtnObs)
        pathBook.removeObserver(bookObs)
        pathChapter.removeObserver(chapterObs)
        pathStartVerse.removeObserver(startVerseObs)
    }

    companion object {
        fun factory(
            gameRepo: GameRepo,
            bibleRepo: BibleRepo,
            kDispatchers: KDispatchers
        ) = newViewModelFactory {
            JourneyEditorVM(gameRepo, bibleRepo, kDispatchers)
        }
    }
}
