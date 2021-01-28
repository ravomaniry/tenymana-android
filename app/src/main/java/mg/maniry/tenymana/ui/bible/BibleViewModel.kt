package mg.maniry.tenymana.ui.bible

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import mg.maniry.tenymana.gameLogic.models.BibleVerse
import mg.maniry.tenymana.repositories.BibleRepo
import mg.maniry.tenymana.repositories.Book
import mg.maniry.tenymana.utils.KDispatchers
import mg.maniry.tenymana.utils.newViewModelFactory

class BibleViewModel(
    private val bibleRepo: BibleRepo,
    private val kDispatchers: KDispatchers
) : ViewModel() {
    private var books: List<Book> = emptyList()
    private val _bookNames = MutableLiveData<List<String>>(emptyList())
    val bookNames: LiveData<List<String>> = _bookNames
    private val _book = MutableLiveData<String?>(null)
    val book: LiveData<String?> = _book
    private val _chapter = MutableLiveData<Int?>(null)
    val chapter: LiveData<Int?> = _chapter
    private val _chapters = MutableLiveData<List<Int>?>(null)
    val chapters: LiveData<List<Int>?> = _chapters
    private val _verses = MutableLiveData<List<BibleVerse>?>(null)
    val verses: LiveData<List<BibleVerse>?> = _verses
    private val _displayChapter = MutableLiveData<String>("")
    val displayChapter: LiveData<String> = _displayChapter
    val shouldClose = MutableLiveData(false)

    private val chapterObs = Observer<Any?> {
        _displayChapter.postValue("${book.value ?: ""}: ${chapter.value ?: ""}")
    }

    fun onBookSelect(index: Int) {
        val book = books[index]
        _book.postValue(book.name)
        val chapters = List(book.chapters) { it + 1 }
        _chapters.postValue(chapters)
    }

    fun onChapterSelect(value: Int) {
        viewModelScope.launch(kDispatchers.main) {
            val book = _book.value
            if (book != null) {
                val verses = bibleRepo.getChapter(book, value).mapIndexed { i, content ->
                    BibleVerse.fromText(book, value, i + 1, content)
                }
                _verses.postValue(verses)
            }
        }
    }

    fun close() {
        shouldClose.postValue(true)
    }

    private fun initBooks() {
        viewModelScope.launch(kDispatchers.main) {
            books = bibleRepo.getBooks()
            _bookNames.postValue(books.map { it.name })
        }
    }

    init {
        _book.observeForever(chapterObs)
        _chapter.observeForever(chapterObs)
        initBooks()
    }

    override fun onCleared() {
        super.onCleared()
        _book.removeObserver(chapterObs)
        _chapter.removeObserver(chapterObs)
    }

    companion object {
        fun factory(bibleRepo: BibleRepo, kDispatchers: KDispatchers) = newViewModelFactory {
            BibleViewModel(bibleRepo, kDispatchers)
        }
    }
}
