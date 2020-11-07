package mg.maniry.tenymana.repositories

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import kotlinx.coroutines.runBlocking
import mg.maniry.tenymana.api.FsHelper
import mg.maniry.tenymana.gameLogic.models.Score
import mg.maniry.tenymana.repositories.models.Journey
import mg.maniry.tenymana.repositories.models.Progress
import mg.maniry.tenymana.repositories.models.Session
import mg.maniry.tenymana.utils.AssetsWrapper
import mg.maniry.tenymana.utils.verifyOnce
import mg.maniry.tenymana.utils.verifyTimes
import org.junit.Rule
import org.junit.Test

class GameRepoTest {
    @get:Rule
    val liveDatarule = InstantTaskExecutorRule()

    @Test
    fun init_userExists() {
        val fs: FsHelper = mock {
            onBlocking { list("123/journey") } doReturn listOf("1.json", "2.json")
            onBlocking { readJson("123/journey/1.json", Journey::class.java) } doReturn j("1")
            onBlocking { readJson("123/journey/2.json", Journey::class.java) } doReturn j("2")
            onBlocking { readJson("123/progress/1.json", Progress::class.java) } doReturn p()
            onBlocking { readJson("123/progress/2.json", Progress::class.java) } doReturn null
        }
        runBlocking {
            val repo = GameRepoImpl(fs)
            repo.initialize("123")
            assertThat(repo.sessions.value).isEqualTo(
                listOf(
                    Session(j("1"), p()),
                    Session(j("2"), Progress("2"))
                )
            )
        }
    }

    @Test
    fun init_newUser() {
        val content = "{\"id\": \"1\"}"
        val assets: AssetsWrapper = mock {
            on { list("journey") } doReturn arrayOf("test.json")
            on { readText("journey/test.json") } doReturn content
        }
        val fs: FsHelper = mock {
            on { this.assets } doReturn assets
            onBlocking { list("111/journey") } doReturn emptyList()
            onBlocking { exists(any()) } doReturn false
        }
        runBlocking {
            val repo = GameRepoImpl(fs)
            repo.initialize("111")
            // list: once before assets copy and once after
            verifyTimes(fs, 2).list("111/journey")
            // copy from assets
            verifyOnce(assets).readText("journey/test.json")
            verifyOnce(fs).writeText("111/journey/test.json", content)
        }
    }

    private fun j(id: String) = Journey(id, "", "", emptyList())

    private fun p() = Progress("1", 10, listOf(listOf(Score(20, 3))), emptyList())
}
