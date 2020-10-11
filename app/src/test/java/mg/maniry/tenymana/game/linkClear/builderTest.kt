package mg.maniry.tenymana.game.linkClear

import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.mock
import mg.maniry.tenymana.game.models.*
import mg.maniry.tenymana.utils.Random
import mg.maniry.tenymana.utils.verifyOnce
import org.junit.Test

@Suppress("unchecked_cast")
class BuilderTest {
    @Test
    fun buildGrid() {
        data class TestCase(
            val text: String,
            val w: Int,
            val wordsOrder: List<String>,
            val cells: List<List<Point?>>,
            val origins: List<Int>,
            val directions: List<Point>,
            val directionsQ: List<MutableList<Point>>
        )

        val tests = listOf(
            TestCase(
                text = "Abc àbc",
                w = 4,
                wordsOrder = listOf("Abc"),
                origins = listOf(0),
                directions = listOf(RIGHT),
                directionsQ = listOf(mutableListOf(UP, RIGHT)),
                cells = listOf(
                    listOf(Point(0, 0), Point(0, 1), Point(0, 2), null),
                    listOf(null, null, null, null)
                )
            ),
            TestCase(
                text = "Abc de fgh ij IJ",
                w = 4,
                wordsOrder = listOf("de", "Abc", "ij", "fgh"),
                origins = listOf(1, 4, 2, 5),
                directions = listOf(LEFT, UP, RIGHT, RIGHT),
                directionsQ = listOf(
                    mutableListOf(LEFT),
                    mutableListOf(UP),
                    mutableListOf(RIGHT, UP_LEFT),
                    mutableListOf(UP, RIGHT)
                ),
                cells = listOf(
                    listOf(Point(2, 1), Point(2, 0), Point(6, 0), Point(6, 1)),
                    listOf(Point(0, 0), Point(4, 0), Point(4, 1), Point(4, 2)),
                    listOf(Point(0, 1), null, null, null),
                    listOf(Point(0, 2), null, null, null),
                    listOf(null, null, null, null)
                )
            )
        )
        for (t in tests) {
            val verse = BibleVerse.fromText("Matio", 1, 1, t.text)
            var wI = -1
            var oI = -1
            var dI = -1
            val random: Random = mock {
                on { from(any<List<*>>()) } doAnswer { c ->
                    val list = c.arguments[0] as List<Any>
                    when {
                        list[0] is Word -> {
                            wI++
                            (list as List<Word>).find { it.value == t.wordsOrder[wI] }
                        }
                        list[0] is Point -> {
                            dI++
                            (list as List<Point>).find { it == t.directions[dI] }
                        }
                        else -> null
                    }
                }
                on { int(any(), any()) } doAnswer {
                    oI++
                    t.origins[oI]
                }
            }
            val grid = buildLinkGrid(verse, random, t.w)
            assertThat(grid).isEqualTo(Grid(t.cells))
            for (dirQ in t.directionsQ) {
                verifyOnce(random).from(dirQ)
            }
        }
    }
}
