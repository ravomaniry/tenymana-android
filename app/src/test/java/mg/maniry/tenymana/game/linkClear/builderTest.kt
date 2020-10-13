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
    fun oneWord() {
        testBuildGrid(
            text = "Abc àbc",
            wordsOrder = listOf("Abc"),
            origins = listOf(0),
            directions = listOf(RIGHT),
            directionsQ = listOf(mutableListOf(UP, RIGHT)),
            cells = listOf(
                listOf(Point(0, 0), Point(0, 1), Point(0, 2), null),
                listOf(null, null, null, null)
            )
        )
    }

    @Test
    fun manyWords() {
        testBuildGrid(
            text = "Abc de fgh ij IJ",
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
    }

    private fun testBuildGrid(
        text: String,
        wordsOrder: List<String>,
        cells: List<List<Point?>>,
        origins: List<Int>,
        directions: List<Point>,
        directionsQ: List<MutableList<Point>>
    ) {
        val verse = BibleVerse.fromText("Matio", 1, 1, text)
        var wI = -1
        var oI = -1
        var dI = -1
        val random: Random = mock {
            on { from(any<List<*>>()) } doAnswer { c ->
                val list = c.arguments[0] as List<Any>
                when {
                    list[0] is Word -> {
                        wI++
                        (list as List<Word>).find { it.value == wordsOrder[wI] }
                    }
                    list[0] is Point -> {
                        dI++
                        (list as List<Point>).find { it == directions[dI] }
                    }
                    else -> null
                }
            }
            on { int(any(), any()) } doAnswer {
                oI++
                origins[oI]
            }
        }
        val grid = buildLinkGrid(verse, random, 4)
        assertThat(grid).isEqualTo(Grid(cells))
        for (dirQ in directionsQ) {
            verifyOnce(random).from(dirQ)
        }
    }
}
