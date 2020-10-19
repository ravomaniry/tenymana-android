package mg.maniry.tenymana.game.sharedLogic.grid

import com.google.common.truth.Truth.assertThat
import mg.maniry.tenymana.game.models.Move
import mg.maniry.tenymana.game.models.MutableGrid
import mg.maniry.tenymana.game.models.Point
import mg.maniry.tenymana.game.models.Point.Companion.DOWN
import mg.maniry.tenymana.game.models.Point.Companion.LEFT
import org.junit.Test

class GravityTest {
    @Test
    fun down() {
        testGravity(
            values = listOf(
                Pair(Point(1, 1), "aa"),
                Pair(Point(2, 0), "bb")
            ),
            gravity = listOf(DOWN),
            cells = mutableListOf<MutableList<String?>>(
                mutableListOf(null, "aa", "bb", null),
                mutableListOf(null, null, null, null),
                mutableListOf(null, null, null, null)
            ),
            diff = listOf(Move.xy(1, 1, 1, 0))
        )
    }

    @Test
    fun downLeft() {
        testGravity(
            values = listOf(
                Pair(Point(0, 3), "ca"),
                Pair(Point(1, 1), "aa"),
                Pair(Point(3, 0), "ba"),
                Pair(Point(3, 2), "bb"),
                Pair(Point(3, 3), "bc")
            ),
            gravity = listOf(DOWN, LEFT),
            cells = mutableListOf<MutableList<String?>>(
                mutableListOf("ca", "aa", "ba", null),
                mutableListOf("bb", null, null, null),
                mutableListOf("bc", null, null, null),
                mutableListOf(null, null, null, null),
                mutableListOf(null, null, null, null)
            ),
            diff = listOf(
                Move.xy(0, 3, 0, 0),
                Move.xy(3, 2, 0, 1),
                Move.xy(3, 3, 0, 2),
                Move.xy(3, 0, 2, 0),
                Move.xy(1, 1, 1, 0)
            )
        )
    }

    private fun testGravity(
        values: List<Pair<Point, String>>,
        gravity: List<Point>,
        cells: MutableList<MutableList<String?>>,
        diff: List<Move>
    ) {
        val grid = MutableGrid<String>(4)
        for (v in values) {
            grid.set(v.first.x, v.first.y, v.second)
        }
        val result = grid.applyGravity(gravity)
        assertThat(grid).isEqualTo(MutableGrid(4, cells))
        assertThat(result).isEqualTo(diff)
    }
}
