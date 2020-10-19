package mg.maniry.tenymana.game.sharedLogic.grid

import com.google.common.truth.Truth.assertThat
import mg.maniry.tenymana.game.models.CharAddress
import mg.maniry.tenymana.game.models.MutableGrid
import mg.maniry.tenymana.game.models.Point
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
        val grid = MutableGrid<CharAddress>(width)
        for (p in filled) {
            grid.set(p.x, p.y, CharAddress(p.x, p.y))
        }
        assertThat(grid.calcOrigins(visibleH)).isEqualTo(result)
    }
}
