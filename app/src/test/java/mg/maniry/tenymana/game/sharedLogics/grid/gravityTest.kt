package mg.maniry.tenymana.game.sharedLogics.grid

import com.google.common.truth.Truth
import mg.maniry.tenymana.game.models.MutableGrid
import mg.maniry.tenymana.game.models.Point
import mg.maniry.tenymana.game.models.Point.Companion.DOWN
import mg.maniry.tenymana.game.models.Point.Companion.LEFT
import org.junit.Test

class GravityTest {
    @Test
    fun down() {
        testGravity(
            cells = listOf(
                Pair(Point(1, 1), Point(0, 0)),
                Pair(Point(2, 0), Point(1, 1))
            ),
            gravity = listOf(DOWN),
            result = mutableListOf<MutableList<Point?>>(
                mutableListOf(null, Point(0, 0), Point(1, 1), null),
                mutableListOf(null, null, null, null),
                mutableListOf(null, null, null, null)
            )
        )
    }

    @Test
    fun downLeft() {
        testGravity(
            cells = listOf(
                Pair(Point(0, 3), Point(2, 0)),
                Pair(Point(1, 1), Point(0, 0)),
                Pair(Point(3, 0), Point(1, 0)),
                Pair(Point(3, 2), Point(1, 1)),
                Pair(Point(3, 3), Point(1, 2))
            ),
            gravity = listOf(DOWN, LEFT),
            result = mutableListOf<MutableList<Point?>>(
                mutableListOf(Point(2, 0), Point(0, 0), Point(1, 0), null),
                mutableListOf(Point(1, 1), null, null, null),
                mutableListOf(Point(1, 2), null, null, null),
                mutableListOf(null, null, null, null),
                mutableListOf(null, null, null, null)
            )
        )
    }

    private fun testGravity(
        cells: List<Pair<Point, Point>>,
        gravity: List<Point>,
        result: MutableList<MutableList<Point?>>
    ) {
        val grid = MutableGrid<Point>(4).apply {
            for (c in cells) {
                set(c.first.x, c.first.y, c.second)
            }
            applyGravity(gravity)
        }
        Truth.assertThat(grid).isEqualTo(MutableGrid(4, result))
    }
}
