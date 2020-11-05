package mg.maniry.tenymana.repositories.models

import com.google.common.truth.Truth.assertThat
import mg.maniry.tenymana.gameLogic.models.Score
import org.junit.Test

class SessionTest {
    @Test
    fun journeySize() {
        val journey = Journey(
            "ab",
            paths = listOf(
                Path("a", "...", "Matio", 1, 10, 20),
                Path("a", "...", "Matio", 2, 1, 5)
            )
        )
        assertThat(journey.size).isEqualTo(16)
    }

    @Test
    fun progressSize() {
        testProgressSize(emptyList(), 0)
        testProgressSize(listOf(listOf(Score(20, 1), Score(10, 1))), 2)
        testProgressSize(
            scores = listOf(
                listOf(Score(20, 1), Score(10, 1)),
                listOf(Score(20, 1))
            ),
            size = 3
        )
    }

    private fun testProgressSize(scores: List<List<Score>>, size: Int) {
        assertThat(Progress("ab", scores = scores).size).isEqualTo(size)
    }
}
