package mg.maniry.tenymana.gameLogic.linkClear

import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.doReturnConsecutively
import com.nhaarman.mockitokotlin2.mock
import mg.maniry.tenymana.gameLogic.models.BibleVerse
import mg.maniry.tenymana.gameLogic.models.CharAddress
import mg.maniry.tenymana.gameLogic.models.Grid
import mg.maniry.tenymana.gameLogic.models.Word
import mg.maniry.tenymana.utils.Random
import org.junit.Test

@Suppress("unchecked_cast")
class BuilderTest {
    @Test
    fun oneWord() {
        testBuildGrid(
            text = "Abc àbc",
            wordsOrder = listOf("Abc"),
            randoms = listOf(1.0, 1.0, 0.5),
            cells = listOf(
                listOf(charA(0, 0), charA(0, 1), charA(0, 2), null),
                listOf(null, null, null, null)
            )
        )
    }

//    @Test
//    fun manyWords() {
//        testBuildGrid(
//            text = "Abc de fgh ij IJ",
//            wordsOrder = listOf("de", "Abc", "ij", "fgh"),
//            randoms = //
//            directionsQ = listOf(
//                mutableListOf(LEFT),
//                mutableListOf(UP),
//                mutableListOf(RIGHT, UP_LEFT),
//                mutableListOf(UP, RIGHT)
//            ),
//            cells = listOf(
//                listOf(charA(2, 1), charA(2, 0), charA(6, 0), charA(6, 1)),
//                listOf(charA(0, 0), charA(4, 0), charA(4, 1), charA(4, 2)),
//                listOf(charA(0, 1), null, null, null),
//                listOf(charA(0, 2), null, null, null),
//                listOf(null, null, null, null)
//            )
//        )
//    }

//    @Test
//    fun ingoreLong_and_OneChar_Words() {
//        // one-char words are filtered out before loop and random
//        testBuildGrid(
//            text = "An'i cdefghijklmn",
//            wordsOrder = listOf("cdefghijklmn", "An"),
//            origins = listOf(0, 1),
//            directions = listOf(LEFT),
//            directionsQ = listOf(mutableListOf(LEFT)),
//            cells = listOf(
//                listOf(charA(0, 1), charA(0, 0), null, null),
//                listOf(null, null, null, null)
//            )
//        )
//    }

    private fun testBuildGrid(
        text: String,
        wordsOrder: List<String>,
        cells: List<List<CharAddress?>>,
        randoms: List<Double>
    ) {
        val verse = BibleVerse.fromText("Matio", 1, 1, text)
        var wI = -1
        val random: Random = mock {
            on { double() } doReturnConsecutively randoms
            on { from(any<List<Word>>()) } doAnswer {
                wI++
                (it.arguments[0] as List<Word>).find { w -> w.value == wordsOrder[wI] }
            }
        }
        val grid = buildLinkGrid(verse, random, 4, 8)
        assertThat(grid).isEqualTo(Grid(cells))
    }

    private fun charA(wI: Int, cI: Int) = CharAddress(wI, cI)
}
