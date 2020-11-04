package mg.maniry.tenymana.gameLogic.shared.grid

import com.google.common.truth.Truth.assertThat
import mg.maniry.tenymana.gameLogic.linkClear.LinkClearPuzzle
import mg.maniry.tenymana.gameLogic.models.*
import org.junit.Test

class OriginsTest {
    @Test
    fun initial() {
        testOrigins(listOf(), 3, 5, listOf(Point(0, 0), Point(1, 0), Point(2, 0)))
    }

    @Test
    fun withValues() {
        testOrigins(
            filled = listOf(Point(0, 0), Point(1, 0), Point(0, 1)),
            width = 3,
            visibleH = 4,
            result = listOf(
                Point(0, 0), Point(1, 0), Point(2, 0),
                Point(0, 1), Point(1, 1),
                Point(0, 2)
            )
        )
    }

    @Test
    fun truncated() {
        testOrigins(
            filled = listOf(Point(0, 0), Point(0, 1), Point(0, 2), Point(0, 3)),
            width = 2,
            visibleH = 3,
            result = listOf(
                Point(0, 0), Point(1, 0),
                Point(0, 1),
                Point(0, 2)
            )
        )
    }

    private fun testOrigins(
        filled: List<Point>,
        width: Int,
        visibleH: Int,
        result: List<Point>
    ) {
        val grid =
            MutableGrid<CharAddress>(width)
        for (p in filled) {
            grid.set(p.x, p.y, CharAddress(p.x, p.y))
        }
        assertThat(grid.calcOrigins(visibleH)).isEqualTo(result)
    }

    @Test
    fun allMovesTruncatedByH() {
        // |   |   |   |
        // | D | E | F |
        // | A | B | C |
        val cells = listOf(
            listOf(ca(0, 0), ca(0, 1), ca(0, 2)),
            listOf(ca(1, 0), ca(1, 1), ca(1, 2)),
            listOf(null, null, null)
        )
        val word = Word.fromValue("ghi", 2, false)
        val moves = listOf(
            MoveWithScore(Move.xy(0, 0, 0, 1), -3.0 + 2.0 / 3),
            MoveWithScore(Move.xy(0, 0, 1, 1), -2.0 + 1),
            MoveWithScore(Move.xy(0, 0, 1, 0), -2.0 + 2),
            MoveWithScore(Move.xy(2, 0, -1, 0), -2.0 + 2),
            MoveWithScore(Move.xy(2, 0, -1, 1), -2.0 + 1),
            MoveWithScore(Move.xy(0, 1, 1, 0), -2.0 + 1),
            MoveWithScore(Move.xy(2, 1, -1, 0), -2.0 + 1),
            MoveWithScore(Move.xy(0, 2, 1, 0), -2.0 + 0),
            MoveWithScore(Move.xy(0, 2, 1, -1), -2.0 + 1),
            MoveWithScore(Move.xy(0, 2, 0, -1), -3.0 + 2.0 / 3),
            MoveWithScore(Move.xy(2, 2, -1, 0), -2.0 + 0)
        )
        testMoves(cells, 4, word, moves)
    }

    @Test
    fun allMovesTruncateRow2() {
        // |   |   |   |
        // | D | E | F |
        // | A | B | C |
        val cells = listOf(
            listOf(ca(0, 0), ca(0, 1), ca(0, 2)),
            listOf(ca(1, 0), ca(1, 1), ca(1, 2)),
            listOf(null, null, null)
        )
        val word = Word.fromValue("ghi", 2, false)
        val moves = listOf(
            MoveWithScore(Move.xy(0, 0, 0, 1), -3.0 + 2.0 / 3),
            MoveWithScore(Move.xy(0, 0, 1, 1), -2.0 + 1),
            MoveWithScore(Move.xy(0, 0, 1, 0), -2.0 + 2),
            MoveWithScore(Move.xy(2, 0, -1, 0), -2.0 + 2),
            MoveWithScore(Move.xy(2, 0, -1, 1), -2.0 + 1),
            MoveWithScore(Move.xy(0, 1, 0, 1), -3.0 + 1.0 / 3),
            MoveWithScore(Move.xy(0, 1, 1, 0), -2.0 + 1),
            MoveWithScore(Move.xy(2, 1, -1, 0), -2.0 + 1),
            MoveWithScore(Move.xy(0, 2, 1, 0), -2.0 + 0),
            MoveWithScore(Move.xy(0, 2, 1, -1), -2.0 + 1),
            MoveWithScore(Move.xy(0, 2, 0, -1), -3.0 + 2.0 / 3),
            MoveWithScore(Move.xy(2, 2, -1, 0), -2.0 + 0)
        )
        testMoves(cells, 5, word, moves)
    }

    private fun testMoves(
        cells: List<List<CharAddress?>>,
        visibleH: Int,
        word: Word,
        moves: List<MoveWithScore>
    ) {
        val grid = Grid(cells)
        val result =
            grid.calcScoredMoves(visibleH, word, LinkClearPuzzle.directions, LinkClearPuzzle.gravity)
        assertThat(result).isEqualTo(moves)
    }

    private fun ca(wI: Int, cI: Int) = CharAddress(wI, cI)
}
