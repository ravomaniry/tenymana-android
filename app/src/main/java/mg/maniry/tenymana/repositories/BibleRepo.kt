package mg.maniry.tenymana.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mg.maniry.tenymana.api.FsHelper
import mg.maniry.tenymana.gameLogic.models.BibleVerse
import mg.maniry.tenymana.repositories.dao.BibleDao
import mg.maniry.tenymana.repositories.setupUtils.copyAssets

data class Book(val name: String, val chapters: Int)

private data class BooksContainer(val value: List<Book>)

interface BibleRepo {
    suspend fun setup()
    suspend fun get(book: String, chapter: Int, minV: Int, maxV: Int): List<BibleVerse>
    suspend fun getSingle(book: String, chapter: Int, verse: Int): BibleVerse?
    suspend fun getChapter(book: String, chapter: Int): List<String>
    suspend fun getBooks(): List<Book>
}

class BibleRepoImpl(
    private val fs: FsHelper
) : BibleRepo {
    private val dirName = "bible"
    private val setupFileName = "$dirName/_setup.txt"
    private val booksFileName = "$dirName/_books.json"
    private val dao = BibleDao(fs)

    override suspend fun get(book: String, chapter: Int, minV: Int, maxV: Int): List<BibleVerse> {
        return dao.get(book, chapter, minV, maxV)
    }

    override suspend fun getSingle(book: String, chapter: Int, verse: Int): BibleVerse? {
        val list = get(book, chapter, verse, verse)
        return if (list.isEmpty()) null else list.first()
    }

    override suspend fun getBooks(): List<Book> {
        return fs.readJson(booksFileName, BooksContainer::class.java)?.value ?: emptyList()
    }

    override suspend fun getChapter(book: String, chapter: Int): List<String> {
        return dao.readChapter(book, chapter)?.verses ?: emptyList()
    }

    override suspend fun setup() {
        withContext(Dispatchers.IO) {
            if (!fs.exists(setupFileName)) {
                fs.copyAssets(dirName, dirName)
                fs.writeText(setupFileName, "done")
            }
        }
    }
}
