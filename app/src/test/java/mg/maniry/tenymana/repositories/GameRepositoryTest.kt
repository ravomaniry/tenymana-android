package mg.maniry.tenymana.repositories

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import kotlinx.coroutines.runBlocking
import mg.maniry.tenymana.api.FsHelper
import mg.maniry.tenymana.game.models.Journey
import mg.maniry.tenymana.game.models.Progress
import mg.maniry.tenymana.game.models.Session
import org.junit.Rule
import org.junit.Test

class GameRepositoryTest {
    @get:Rule
    val liveDatarule = InstantTaskExecutorRule()

    @Test
    fun initialize() {
        val fs: FsHelper = mock {
            onBlocking { list("journey") } doReturn listOf("1.json", "2.json")
            onBlocking { readJson("journey/1.json", Journey::class.java) } doReturn j("1")
            onBlocking { readJson("journey/2.json", Journey::class.java) } doReturn j("2")
            onBlocking { readJson("progress/1.json", Progress::class.java) } doReturn p()
            onBlocking { readJson("progress/2.json", Progress::class.java) } doReturn null
        }
        val repo = GameRepositoryImpl(fs)
        runBlocking { repo.initialize() }
        assertThat(repo.sessions.value).isEqualTo(
            listOf(
                Session(j("1"), p()),
                Session(j("2"), Progress.empty("2"))
            )
        )
    }

    private fun j(id: String) = Journey(id, "", "", emptyList())
    private fun p() = Progress("1", 10, listOf(10), emptyList())
}
