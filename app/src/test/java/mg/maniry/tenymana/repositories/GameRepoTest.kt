package mg.maniry.tenymana.repositories

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import kotlinx.coroutines.runBlocking
import mg.maniry.tenymana.api.FsHelper
import mg.maniry.tenymana.repositories.models.Journey
import mg.maniry.tenymana.repositories.models.Progress
import mg.maniry.tenymana.repositories.models.Session
import org.junit.Rule
import org.junit.Test

class GameRepoTest {
    @get:Rule
    val liveDatarule = InstantTaskExecutorRule()

    @Test
    fun initialize() {
        val fs: FsHelper = mock {
            onBlocking { list("123/journey") } doReturn listOf("1.json", "2.json")
            onBlocking { readJson("123/journey/1.json", Journey::class.java) } doReturn j("1")
            onBlocking { readJson("123/journey/2.json", Journey::class.java) } doReturn j("2")
            onBlocking { readJson("123/progress/1.json", Progress::class.java) } doReturn p()
            onBlocking { readJson("123/progress/2.json", Progress::class.java) } doReturn null
        }
        val repo = GameRepoImpl(fs)
        runBlocking { repo.initialize("123") }
        assertThat(repo.sessions.value).isEqualTo(
            listOf(
                Session(j("1"), p()),
                Session(j("2"), Progress("2"))
            )
        )
    }

    private fun j(id: String) = Journey(id, "", "", emptyList())

    private fun p() = Progress("1", 10, listOf(listOf(20)), emptyList())
}
