package mg.maniry.tenymana.repositories

import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import kotlinx.coroutines.runBlocking
import mg.maniry.tenymana.api.FsHelper
import mg.maniry.tenymana.gameLogic.models.BibleVerse
import mg.maniry.tenymana.repositories.models.BibleChapter
import org.junit.Test

class BibleRepoTest {
    @Test
    fun get() {
        val chapter = BibleChapter(
            "Matio",
            1,
            listOf("1", "2", "3", "4", "5", "6")
        )
        val fs: FsHelper = mock {
            onBlocking { readJson("bible/Matio-1", BibleChapter::class.java) } doReturn chapter
        }
        val repo = BibleRepoImpl(fs)
        runBlocking {
            assertThat(repo.getSingle("Matio", 1, 1))
                .isEqualTo(BibleVerse.fromText("Matio", 1, 1, "1"))
            assertThat(repo.getSingle("Matio", 1, 6))
                .isEqualTo(BibleVerse.fromText("Matio", 1, 6, "6"))
            assertThat(repo.getSingle("Matio", 1, 7)).isNull()
            assertThat(repo.get("Matio", 1, 2, 5)).isEqualTo(
                listOf(
                    BibleVerse.fromText("Matio", 1, 2, "2"),
                    BibleVerse.fromText("Matio", 1, 3, "3"),
                    BibleVerse.fromText("Matio", 1, 4, "4"),
                    BibleVerse.fromText("Matio", 1, 5, "5")
                )
            )
        }
    }
}
