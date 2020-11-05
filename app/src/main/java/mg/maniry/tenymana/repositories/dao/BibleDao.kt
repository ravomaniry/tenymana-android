package mg.maniry.tenymana.repositories.dao

import mg.maniry.tenymana.api.FsHelper
import mg.maniry.tenymana.gameLogic.models.BibleVerse
import mg.maniry.tenymana.repositories.models.BibleChapter

class BibleDao(
    private val fs: FsHelper
) {
    private val dir = "bible"
    private var cache: Pair<String, BibleChapter>? = null

    suspend fun get(book: String, chapter: Int, minV: Int, maxV: Int): List<BibleVerse> {
        val content = readChapter(book, chapter)
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

    private suspend fun readChapter(book: String, chapter: Int): BibleChapter? {
        val path = buildPath(book, chapter)
        if (path == cache?.first) {
            return cache!!.second
        }
        val content = fs.readJson(path, BibleChapter::class.java)
        if (content != null) {
            cache = Pair(path, content)
        }
        return content
    }

    private fun buildPath(book: String, chapter: Int) = "$dir/$book-$chapter.json"
}

