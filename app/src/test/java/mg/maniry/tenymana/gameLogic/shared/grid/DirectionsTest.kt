package mg.maniry.tenymana.gameLogic.shared.grid

import com.google.common.truth.Truth.assertThat
import mg.maniry.tenymana.gameLogic.linkClear.LinkClearPuzzle.Companion.gravity
import mg.maniry.tenymana.gameLogic.models.CharAddress
import mg.maniry.tenymana.gameLogic.models.MutableGrid
import mg.maniry.tenymana.gameLogic.models.Point
import mg.maniry.tenymana.gameLogic.models.Point.Companion.DOWN
import mg.maniry.tenymana.gameLogic.models.Point.Companion.LEFT
import mg.maniry.tenymana.gameLogic.models.Point.Companion.RIGHT
import mg.maniry.tenymana.gameLogic.models.Point.Companion.UP
import mg.maniry.tenymana.gameLogic.models.Word
import org.junit.Test

class DirectionsTest {
    private val grid =
        MutableGrid<CharAddress>(3)

    @Test
    fun enoughSpace_00() {
        testDirections(Point(0, 0), 4, listOf(UP, RIGHT))
    }

    @Test
    fun enoughSpace_20() {
        testDirections(Point(2, 0), 4, listOf(LEFT))
    }

    @Test
    fun noSpace_top() {
        testDirections(Point(0, 0), 2, listOf(RIGHT))
    }

    @Test
    fun noSpace() {
        testDirections(Point(0, 2), 4, listOf(DOWN))
    }

    @Test
    fun limitedChoise() {
        val word = Word.fromValue("Abc", 0)
        val choice = listOf(RIGHT)
        assertThat(grid.calcDirections(choice, Point(0, 0), word, 10, gravity))
            .isEqualTo(listOf(RIGHT))
    }

    private fun testDirections(origin: Point, visiblH: Int, result: List<Point>) {
        val word = Word.fromValue("Abc", 0)
        assertThat(grid.calcDirections(Point.directions, origin, word, visiblH, gravity))
            .isEqualTo(result)
    }
}
