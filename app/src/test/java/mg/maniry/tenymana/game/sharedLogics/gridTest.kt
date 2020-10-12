package mg.maniry.tenymana.game.sharedLogics

import com.google.common.truth.Truth.assertThat
import mg.maniry.tenymana.game.models.*
import org.junit.Test

class GridTest {
    @Test
    fun gravityDown() {
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
    fun gravityDownLeft() {
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
        val grid = MutableGrid(4).apply {
            for (c in cells) {
                set(c.first.x, c.first.y, c.second)
            }
            applyGravity(gravity)
        }
        assertThat(grid).isEqualTo(MutableGrid(4, result))
    }

    @Test
    fun persistOnEmpty_Up() {
        testPersist(
            w = 3,
            delta = Pair(Point(0, 0), Pair(UP, "Abc")),
            result = mutableListOf<MutableList<Point?>>(
                mutableListOf(Point(0, 0), null, null),
                mutableListOf(Point(0, 1), null, null),
                mutableListOf(Point(0, 2), null, null),
                mutableListOf(null, null, null)
            )
        )
    }

    @Test
    fun persistOnEmpty_Left() {
        testPersist(
            w = 3,
            delta = Pair(Point(2, 0), Pair(LEFT, "Abc")),
            result = mutableListOf<MutableList<Point?>>(
                mutableListOf(Point(0, 2), Point(0, 1), Point(0, 0)),
                mutableListOf(null, null, null)
            )
        )
    }

    @Test
    fun persistOnEmpty_Right() {
        testPersist(
            w = 3,
            delta = Pair(Point(0, 0), Pair(RIGHT, "Abc")),
            result = mutableListOf<MutableList<Point?>>(
                mutableListOf(Point(0, 0), Point(0, 1), Point(0, 2)),
                mutableListOf(null, null, null)
            )
        )
    }

    @Test
    fun persistOnEmpty_UpRight() {
        testPersist(
            w = 3,
            delta = Pair(Point(0, 0), Pair(UP_RIGHT, "Abc")),
            result = mutableListOf<MutableList<Point?>>(
                mutableListOf(Point(0, 0), null, null),
                mutableListOf(null, Point(0, 1), null),
                mutableListOf(null, null, Point(0, 2)),
                mutableListOf(null, null, null)
            )
        )
    }

    @Test
    fun persistOnEmpty_UpLeft() {
        testPersist(
            w = 3,
            delta = Pair(Point(2, 0), Pair(UP_LEFT, "Abc")),
            result = mutableListOf<MutableList<Point?>>(
                mutableListOf(null, null, Point(0, 0)),
                mutableListOf(null, Point(0, 1), null),
                mutableListOf(Point(0, 2), null, null),
                mutableListOf(null, null, null)
            )
        )
    }

    // | F | . | . |
    // | C | D | E |
    private val persistOnFilledUpValues = listOf(
        Pair(Point(0, 0), Point(1, 0)),
        Pair(Point(1, 0), Point(1, 1)),
        Pair(Point(2, 0), Point(1, 2)),
        Pair(Point(0, 1), Point(2, 0))
    )

    @Test
    fun persistOnFilled_Up_slideRight() {
        testPersist(
            w = 3,
            values = persistOnFilledUpValues,
            delta = Pair(Point(0, 1), Pair(UP, "Ab")),
            result = mutableListOf<MutableList<Point?>>(
                mutableListOf(Point(1, 0), Point(1, 1), Point(1, 2)),
                mutableListOf(Point(0, 0), Point(2, 0), null),
                mutableListOf(Point(0, 1), null, null),
                mutableListOf(null, null, null)
            )
        )
    }

    @Test
    fun persistFilled_Right_slideRight() {
        testPersist(
            w = 3,
            values = persistOnFilledUpValues,
            delta = Pair(Point(0, 1), Pair(RIGHT, "Ab")),
            result = mutableListOf<MutableList<Point?>>(
                mutableListOf(Point(1, 0), Point(1, 1), Point(1, 2)),
                mutableListOf(Point(0, 0), Point(0, 1), Point(2, 0)),
                mutableListOf(null, null, null)
            )
        )
    }

    @Test
    fun persistFilled_Leftt_slideRight() {
        testPersist(
            w = 3,
            values = persistOnFilledUpValues,
            delta = Pair(Point(1, 1), Pair(LEFT, "Ab")),
            result = mutableListOf<MutableList<Point?>>(
                mutableListOf(Point(1, 0), Point(1, 1), Point(1, 2)),
                mutableListOf(Point(0, 1), Point(0, 0), Point(2, 0)),
                mutableListOf(null, null, null)
            )
        )
    }

    @Test
    fun persistOnFilled_Left_SlideUp() {
        testPersist(
            w = 3,
            values = persistOnFilledUpValues,
            delta = Pair(Point(2, 0), Pair(LEFT, "Ab")),
            result = mutableListOf<MutableList<Point?>>(
                mutableListOf(Point(1, 0), Point(0, 1), Point(0, 0)),
                mutableListOf(Point(2, 0), Point(1, 1), Point(1, 2)),
                mutableListOf(null, null, null)
            )
        )
    }

    @Test
    fun persistOnFilled_Down_SlideUp() {
        testPersist(
            w = 3,
            values = persistOnFilledUpValues,
            delta = Pair(Point(1, 1), Pair(DOWN, "Ab")),
            result = mutableListOf<MutableList<Point?>>(
                mutableListOf(Point(1, 0), Point(0, 1), Point(1, 2)),
                mutableListOf(Point(2, 0), Point(0, 0), null),
                mutableListOf(null, Point(1, 1), null),
                mutableListOf(null, null, null)
            )
        )
    }

    @Test
    fun persistOnFilled_Up_SlideUp() {
        testPersist(
            w = 3,
            values = persistOnFilledUpValues,
            delta = Pair(Point(0, 0), Pair(UP, "Ab")),
            result = mutableListOf<MutableList<Point?>>(
                mutableListOf(Point(0, 0), Point(1, 1), Point(1, 2)),
                mutableListOf(Point(0, 1), null, null),
                mutableListOf(Point(1, 0), null, null),
                mutableListOf(Point(2, 0), null, null),
                mutableListOf(null, null, null)
            )
        )
    }

    // | E | D |   |   |   |
    // | F | G | H |   |   |
    private val diagValues = listOf(
        Pair(Point(0, 0), Point(2, 0)),
        Pair(Point(1, 0), Point(2, 1)),
        Pair(Point(2, 0), Point(2, 2)),
        Pair(Point(0, 1), Point(1, 1)),
        Pair(Point(1, 1), Point(1, 0))
    )

    @Test
    fun persistOnFilled_UpLeft_slideUp() {
        testPersist(
            w = 5,
            values = diagValues,
            delta = Pair(Point(2, 0), Pair(UP_LEFT, "Abc")),
            result = mutableListOf<MutableList<Point?>>(
                mutableListOf(Point(2, 0), Point(2, 1), Point(0, 0), null, null),
                mutableListOf(Point(1, 1), Point(0, 1), Point(2, 2), null, null),
                mutableListOf(Point(0, 2), Point(1, 0), null, null, null),
                mutableListOf(null, null, null, null, null)
            )
        )
    }

    @Test
    fun persistOnFilled_DownRight_slideRight() {
        testPersist(
            w = 5,
            values = diagValues,
            delta = Pair(Point(0, 2), Pair(DOWN_RIGHT, "Abc")),
            result = mutableListOf<MutableList<Point?>>(
                mutableListOf(Point(2, 0), Point(2, 1), Point(0, 2), null, null),
                mutableListOf(Point(1, 1), Point(0, 1), Point(2, 2), null, null),
                mutableListOf(Point(0, 0), Point(1, 0), null, null, null),
                mutableListOf(null, null, null, null, null)
            )
        )
    }

    @Test
    fun persistOnFilled_LeftFromBottomRight_SlideUp() {
        testPersist(
            w = 5,
            values = diagValues,
            delta = Pair(Point(4, 0), Pair(LEFT, "Abc")),
            result = mutableListOf<MutableList<Point?>>(
                mutableListOf(Point(2, 0), Point(2, 1), Point(0, 2), Point(0, 1), Point(0, 0)),
                mutableListOf(Point(1, 1), Point(1, 0), Point(2, 2), null, null),
                mutableListOf(null, null, null, null, null)
            )
        )
    }

    private fun testPersist(
        w: Int,
        values: List<Pair<Point, Point>> = listOf(),
        delta: Pair<Point, Pair<Point, String>>,
        result: MutableList<MutableList<Point?>>
    ) {
        val grid = MutableGrid(w)
        for (v in values) {
            grid.set(v.first.x, v.first.y, v.second)
        }
        val word = Word.fromValue(delta.second.second, 0, false)
        grid.placeWord(delta.first, delta.second.first, word)
        assertThat(grid).isEqualTo(MutableGrid(w, result))
    }
}
