package mg.maniry.tenymana.gameLogic.linkClear

import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.doReturnConsecutively
import com.nhaarman.mockitokotlin2.mock
import mg.maniry.tenymana.gameLogic.models.BibleVerse
import mg.maniry.tenymana.gameLogic.models.CharAddress
import mg.maniry.tenymana.gameLogic.models.Grid
import mg.maniry.tenymana.gameLogic.models.Point
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
            randoms = listOf(1.0, 1.0, 0.5),
            cells = cells,
            solutions = listOf(
                SolutionItem(
                    Grid(cells),
                    listOf(Point(0, 0), Point(1, 0), Point(2, 0))
                )
            )
        )
    }

    @Test
    fun manyWords_ingoreTooShortAndTooLong() {
        val solutions = listOf(
            SolutionItem( // ij
                Grid(
                    listOf(
                        listOf(charA(4, 0), null, null, null),
                        listOf(charA(4, 1), null, null, null),
                        listOf(null, null, null, null)
                    )
                ),
                listOf(Point(0, 0), Point(0, 1))
            ),
            SolutionItem( // Abc
                Grid(
                    listOf(
                        listOf(charA(4, 0), charA(2, 2), charA(2, 1), charA(2, 0)),
                        listOf(charA(4, 1), null, null, null),
                        listOf(null, null, null, null)
                    )
                ),
                listOf(Point(3, 0), Point(2, 0), Point(1, 0))
            ),
            SolutionItem( // de
                Grid(
                    listOf(
                        listOf(charA(4, 0), charA(0, 0), charA(0, 1), charA(2, 0)),
                        listOf(charA(4, 1), charA(2, 2), charA(2, 1), null),
                        listOf(null, null, null, null)
                    )
                ),
                listOf(Point(1, 0), Point(2, 0))
            )
        )
        testBuildGrid(
            text = "De abc ij'k lmnopqrst",
            randoms = listOf(
                1.0, 0.1, 0.1,
                0.1, 0.1, 0.01, 0.1, 1.0, 0.1, 0.1, 0.1,
                0.1, 0.1, 1.0, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1
            ),
            cells = solutions.last().grid.cells,
            solutions = solutions
        )
    }

    private fun testBuildGrid(
        text: String,
        randoms: List<Double>,
        cells: List<List<CharAddress?>>,
        solutions: List<SolutionItem<CharAddress>>
    ) {
        val verse = BibleVerse.fromText("Matio", 1, 1, text)
        val random: Random = mock {
            on { double() } doReturnConsecutively randoms
        }
        val grid = buildLinkGrid(verse, random, 4, 8)
        assertThat(grid).isEqualTo(Pair(Grid(cells), solutions))
    }

    private fun charA(wI: Int, cI: Int) = CharAddress(wI, cI)
}
