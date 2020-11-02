package mg.maniry.tenymana.helpers

import mg.maniry.tenymana.gameLogic.models.BibleVerse
import mg.maniry.tenymana.repositories.BibleRepo

class BibleRepoMock : BibleRepo {
    val getFn = Fn<List<BibleVerse>>()
    val getSingleFn = Fn<BibleVerse?>()
    val setupFn = Fn<Unit>()

    override suspend fun get(book: String, chapter: Int, minV: Int, maxV: Int) =
        getFn(book, chapter, minV, maxV)

    override suspend fun getSingle(book: String, chapter: Int, verse: Int) =
        getSingleFn(book, chapter, verse)

    override suspend fun setup() {
        setupFn()
    }
}
