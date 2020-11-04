package mg.maniry.tenymana.gameLogic.shared.grid

import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.doReturnConsecutively
import com.nhaarman.mockitokotlin2.mock
import mg.maniry.tenymana.gameLogic.linkClear.LinkClearPuzzle
import mg.maniry.tenymana.gameLogic.models.CharAddress
import mg.maniry.tenymana.gameLogic.models.Grid
import mg.maniry.tenymana.gameLogic.models.Move
import mg.maniry.tenymana.gameLogic.models.Word
import mg.maniry.tenymana.utils.Random
import org.junit.Test

class MovesTest {
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
            grid.calcScoredMoves(
                visibleH,
                word,
                LinkClearPuzzle.directions,
                LinkClearPuzzle.gravity
            )
        assertThat(result).isEqualTo(moves)
    }

    @Test
    fun randomMove_AllPositive() {
        testRandomMove(
            withscores = listOf(
                MoveWithScore(Move.xy(0, 0, 0, 0), 1.0),
                MoveWithScore(Move.xy(1, 1, 1, 1), 2.0),
                MoveWithScore(Move.xy(2, 2, 2, 2), 2.0)
            ),
            randoms = listOf(0.75, 0.81, 0.8),
            move = Move.xy(1, 1, 1, 1)
        )
    }

    @Test
    fun randomMove_AllNegatives() {
        testRandomMove(
            withscores = listOf(
                MoveWithScore(Move.xy(0, 0, 0, 0), -2.0),
                MoveWithScore(Move.xy(1, 1, 1, 1), -2.0)
            ),
            randoms = listOf(0.1, 0.2),
            move = Move.xy(1, 1, 1, 1)
        )
    }

    private fun testRandomMove(
        withscores: List<MoveWithScore>,
        randoms: List<Double>,
        move: Move
    ) {
        val random: Random = mock {
            on { double() } doReturnConsecutively randoms
        }
        assertThat(withscores.getRandomByRate(random)).isEqualTo(move)
    }

    private fun ca(wI: Int, cI: Int) = CharAddress(wI, cI)
}
