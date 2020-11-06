package mg.maniry.tenymana.gameLogic.linkClear

import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.doReturnConsecutively
import com.nhaarman.mockitokotlin2.mock
import mg.maniry.tenymana.gameLogic.models.*
import mg.maniry.tenymana.utils.Random
import org.junit.Test

@Suppress("unchecked_cast")
class BuilderTest {
    @Test
    fun oneWord() {
        val cells = listOf(
            listOf(charA(0, 0), charA(0, 1), charA(0, 2), null),
            listOf(null, null, null, null)
        )
        testBuildGrid(
            text = "Abc àbc",
            wordsOrder = listOf("Abc"),
            randoms = listOf(1.0, 1.0, 0.5),
            cells = cells,
            solutions = listOf(SolutionItem(Grid(cells), Move.xy(0, 0, 2, 0)))
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
        randoms: List<Double>,
        cells: List<List<CharAddress?>>,
        solutions: List<SolutionItem<CharAddress>>
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
        assertThat(grid).isEqualTo(Pair(Grid(cells), solutions))
    }

    private fun charA(wI: Int, cI: Int) = CharAddress(wI, cI)
}
