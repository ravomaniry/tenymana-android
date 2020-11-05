package mg.maniry.tenymana.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mg.maniry.tenymana.api.FsHelper
import mg.maniry.tenymana.gameLogic.models.BibleVerse
import mg.maniry.tenymana.repositories.dao.BibleDao
import mg.maniry.tenymana.repositories.models.BibleChapter
import mg.maniry.tenymana.repositories.setupUtils.copyAssets

interface BibleRepo {
    suspend fun setup()
    suspend fun get(book: String, chapter: Int, minV: Int, maxV: Int): List<BibleVerse>
    suspend fun getSingle(book: String, chapter: Int, verse: Int): BibleVerse?
}

class BibleRepoImpl(
    private val fs: FsHelper
) : BibleRepo {
    private val dirName = "bible"
    private val setupFileName = "$dirName/_setup.txt"
    private val dao = BibleDao(fs)

    override suspend fun get(book: String, chapter: Int, minV: Int, maxV: Int): List<BibleVerse> {
        return dao.get(book, chapter, minV, maxV)
    }

    override suspend fun getSingle(book: String, chapter: Int, verse: Int): BibleVerse? {
        val list = get(book, chapter, verse, verse)
        return if (list.isEmpty()) null else list.first()
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
