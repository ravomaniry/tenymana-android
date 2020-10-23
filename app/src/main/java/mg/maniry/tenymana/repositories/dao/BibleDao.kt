package mg.maniry.tenymana.repositories.dao

import mg.maniry.tenymana.api.FsHelper
import mg.maniry.tenymana.gameLogic.models.BibleVerse
import mg.maniry.tenymana.repositories.models.BibleChapter

class BibleDao(
    private val fs: FsHelper
) {
    private val dir = "bible"

    suspend fun get(book: String, chapter: Int, minV: Int, maxV: Int): List<BibleVerse> {
        val content = fs.readJson(buildPath(book, chapter), BibleChapter::class.java)
        val res = mutableListOf<BibleVerse>()
        if (content != null) {
            for (v in minV..maxV) {
                if (v > 0 && v <= content.verses.size) {
                    res.add(BibleVerse.fromText(book, chapter, v, content.verses[v - 1]))
                }
            }
        }
        return res
    }

    private fun buildPath(book: String, chapter: Int) = "$dir/$book-$chapter"
}

