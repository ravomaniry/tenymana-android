package mg.maniry.tenymana.repositories

import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.runBlocking
import mg.maniry.tenymana.api.FsHelper
import mg.maniry.tenymana.gameLogic.models.BibleVerse
import mg.maniry.tenymana.repositories.models.BibleChapter
import mg.maniry.tenymana.utils.AssetsWrapper
import mg.maniry.tenymana.utils.verifyNever
import mg.maniry.tenymana.utils.verifyOnce
import mg.maniry.tenymana.utils.verifyTimes
import org.junit.Test

class BibleRepoTest {
    @Test
    fun get() {
        val ch1 = BibleChapter(
            "Matio",
            1,
            listOf("1", "2", "3", "4", "5", "6")
        )
        val ch2 = BibleChapter(
            "Matio",
            2,
            listOf("1", "2", "3", "4", "5", "6")
        )
        val fs: FsHelper = mock {
            onBlocking { readJson("bible/Matio-1.json", BibleChapter::class.java) } doReturn ch1
            onBlocking { readJson("bible/Matio-2.json", BibleChapter::class.java) } doReturn ch2
        }
        runBlocking {
            val repo = BibleRepoImpl(fs)
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
            assertThat(repo.get("Matio", 2, 4, 5)).isEqualTo(
                listOf(
                    BibleVerse.fromText("Matio", 2, 4, "4"),
                    BibleVerse.fromText("Matio", 2, 5, "5")
                )
            )
            verifyOnce(fs).readJson("bible/Matio-1.json", BibleChapter::class.java)
            verifyOnce(fs).readJson("bible/Matio-2.json", BibleChapter::class.java)
        }
    }

    @Test
    fun setup_new() {
        testSetup(
            files = arrayOf("Matio-1.json", "Marka-1.json", "Lioka-1.json"),
            exists = mapOf(
                Pair("bible/_setup.txt", false),
                Pair("bible/Matio-1.json", false),
                Pair("bible/Marka-1.json", false),
                Pair("bible/Lioka-1.json", true) // already copied
            ),
            readAssets = true,
            assetsReadPaths = listOf("bible/Matio-1.json", "bible/Marka-1.json"),
            chaperWrites = listOf("bible/Matio-1.json", "bible/Marka-1.json"),
            writeLockFile = true
        )
    }

    @Test
    fun setup_existing() {
        testSetup(exists = mapOf(Pair("bible/_setup.txt", true)))
    }

    private fun testSetup(
        files: Array<String> = emptyArray(),
        exists: Map<String, Boolean>,
        readAssets: Boolean = false,
        assetsReadPaths: List<String> = emptyList(),
        chaperWrites: List<String> = emptyList(),
        writeLockFile: Boolean = false
    ) {
        val content = "{\"a\": 1}"
        val assets: AssetsWrapper = mock {
            on { list("bible") } doReturn files
            on { readText(any()) } doReturn content
        }
        val fs: FsHelper = mock {
            on { this.assets } doReturn assets
            onBlocking { exists(any()) } doAnswer { exists[it.arguments[0]] }
        }
        runBlocking {
            val repo = BibleRepoImpl(fs)
            // no action until read
            verifyZeroInteractions(fs)
            // setup
            repo.setup()
            // check if setup file exists + read files + write in data dir
            verifyOnce(fs).exists("bible/_setup.txt")
            if (readAssets) {
                verifyOnce(assets).list("bible")
            } else {
                verifyZeroInteractions(assets)
            }
            // read and write twice since lioka is already copied
            verifyTimes(assets, assetsReadPaths.size).readText(any())
            for (p in assetsReadPaths) {
                verifyOnce(assets).readText(p)
            }
            for (p in chaperWrites) {
                verifyOnce(fs).writeText(p, content)
            }
            if (writeLockFile) {
                verifyOnce(fs).writeText("bible/_setup.txt", "done")
            } else {
                verifyNever(fs).writeText("bible/_setup.txt", "done")
            }
            val writesN = chaperWrites.size + if (writeLockFile) 1 else 0
            verifyTimes(fs, writesN).writeText(any(), any())
        }
    }
}
