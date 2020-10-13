package mg.maniry.tenymana.game.linkClear

import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.mock
import mg.maniry.tenymana.game.models.*
import mg.maniry.tenymana.game.models.Point.Companion.LEFT
import mg.maniry.tenymana.game.models.Point.Companion.RIGHT
import mg.maniry.tenymana.game.models.Point.Companion.UP
import mg.maniry.tenymana.game.models.Point.Companion.UP_LEFT
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
                listOf(char('A', 'a'), char('B', 'b'), char('C', 'c'), null),
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
                listOf(char('E', 'e'), char('D', 'd'), char('I', 'i'), char('J', 'j')),
                listOf(char('A', 'a'), char('F', 'f'), char('G', 'g'), char('H', 'h')),
                listOf(char('B', 'b'), null, null, null),
                listOf(char('C', 'c'), null, null, null),
                listOf(null, null, null, null)
            )
        )
    }

    @Test
    fun ingoreLongWords() {
        testBuildGrid(
            text = "Ab cdefghijklmn",
            wordsOrder = listOf("cdefghijklmn", "Ab"),
            origins = listOf(0, 1),
            directions = listOf(LEFT),
            directionsQ = listOf(mutableListOf(LEFT)),
            cells = listOf(
                listOf(char('B', 'b'), char('A', 'a'), null, null),
                listOf(null, null, null, null)
            )
        )
    }

    private fun testBuildGrid(
        text: String,
        wordsOrder: List<String>,
        cells: List<List<Character?>>,
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
        val grid = buildLinkGrid(verse, random, 4, 8)
        assertThat(grid).isEqualTo(Grid(cells))
        for (dirQ in directionsQ) {
            verifyOnce(random).from(dirQ)
        }
    }

    private fun char(value: Char, compValue: Char): Character {
        return Character(value, compValue)
    }
}
