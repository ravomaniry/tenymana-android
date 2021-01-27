package mg.maniry.tenymana.ui.bible

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import mg.maniry.tenymana.repositories.BibleRepo
import mg.maniry.tenymana.utils.newViewModelFactory

class BibleViewModel(
    private val bibleRepo: BibleRepo
) : ViewModel() {
    val book = MutableLiveData<String?>(null)
    val chapter = MutableLiveData<Int?>(null)
    val verses = MutableLiveData<List<String>?>(null)
    val displayChapter = MutableLiveData<String>("")

    private val chapterObs = Observer<Any> {
        displayChapter.postValue("${book.value ?: ""}: ${chapter.value ?: ""}")
    }

    fun close() {

    }

    companion object {
        fun factory(bibleRepo: BibleRepo) = newViewModelFactory {
            BibleViewModel(bibleRepo)
        }
    }
}
