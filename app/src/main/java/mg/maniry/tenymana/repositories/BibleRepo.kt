package mg.maniry.tenymana.repositories

import mg.maniry.tenymana.api.FsHelper
import mg.maniry.tenymana.gameLogic.models.BibleVerse
import mg.maniry.tenymana.repositories.dao.BibleDao

interface BibleRepo {
    suspend fun get(book: String, chapter: Int, minV: Int, maxV: Int): List<BibleVerse>

    suspend fun getSingle(book: String, chapter: Int, verse: Int): BibleVerse?
}

class BibleRepoImpl(
    fs: FsHelper
) : BibleRepo {
    private val dao = BibleDao(fs)

    override suspend fun get(book: String, chapter: Int, minV: Int, maxV: Int): List<BibleVerse> {
        return dao.get(book, chapter, minV, maxV)
    }

    override suspend fun getSingle(book: String, chapter: Int, verse: Int): BibleVerse? {
        val list = get(book, chapter, verse, verse)
        return if (list.isEmpty()) null else list.first()
    }
}
