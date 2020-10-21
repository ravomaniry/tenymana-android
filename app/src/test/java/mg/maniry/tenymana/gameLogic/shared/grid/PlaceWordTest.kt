package mg.maniry.tenymana.gameLogic.shared.grid

import com.google.common.truth.Truth.assertThat
import mg.maniry.tenymana.gameLogic.linkClear.LinkClearPuzzle.Companion.gravity
import mg.maniry.tenymana.gameLogic.models.CharAddress
import mg.maniry.tenymana.gameLogic.models.MutableGrid
import mg.maniry.tenymana.gameLogic.models.Point
import mg.maniry.tenymana.gameLogic.models.Point.Companion.DOWN
import mg.maniry.tenymana.gameLogic.models.Point.Companion.DOWN_RIGHT
import mg.maniry.tenymana.gameLogic.models.Point.Companion.LEFT
import mg.maniry.tenymana.gameLogic.models.Point.Companion.RIGHT
import mg.maniry.tenymana.gameLogic.models.Point.Companion.UP
import mg.maniry.tenymana.gameLogic.models.Point.Companion.UP_LEFT
import mg.maniry.tenymana.gameLogic.models.Point.Companion.UP_RIGHT
import mg.maniry.tenymana.gameLogic.models.Word
import org.junit.Test

class PlaceWordTest {
    @Test
    fun persistOnEmpty_Up() {
        testPlaceWord(
            w = 3,
            delta = Pair(Point(0, 0), Pair(UP, "Abc")),
            result = mutableListOf<MutableList<CharAddress?>>(
                mutableListOf(CharAddress(0, 0), null, null),
                mutableListOf(CharAddress(0, 1), null, null),
                mutableListOf(CharAddress(0, 2), null, null),
                mutableListOf(null, null, null)
            )
        )
    }

    @Test
    fun persistOnEmpty_Left() {
        testPlaceWord(
            w = 3,
            delta = Pair(Point(2, 0), Pair(LEFT, "Abc")),
            result = mutableListOf<MutableList<CharAddress?>>(
                mutableListOf(CharAddress(0, 2), CharAddress(0, 1), CharAddress(0, 0)),
                mutableListOf(null, null, null)
            )
        )
    }

    @Test
    fun persistOnEmpty_Right() {
        testPlaceWord(
            w = 3,
            delta = Pair(Point(0, 0), Pair(RIGHT, "Abc")),
            result = mutableListOf<MutableList<CharAddress?>>(
                mutableListOf(CharAddress(0, 0), CharAddress(0, 1), CharAddress(0, 2)),
                mutableListOf(null, null, null)
            )
        )
    }

    @Test
    fun persistOnEmpty_UpRight() {
        testPlaceWord(
            w = 3,
            delta = Pair(Point(0, 0), Pair(UP_RIGHT, "Abc")),
            result = mutableListOf<MutableList<CharAddress?>>(
                mutableListOf(CharAddress(0, 0), null, null),
                mutableListOf(null, CharAddress(0, 1), null),
                mutableListOf(null, null, CharAddress(0, 2)),
                mutableListOf(null, null, null)
            )
        )
    }

    @Test
    fun persistOnEmpty_UpLeft() {
        testPlaceWord(
            w = 3,
            delta = Pair(Point(2, 0), Pair(UP_LEFT, "Abc")),
            result = mutableListOf<MutableList<CharAddress?>>(
                mutableListOf(null, null, CharAddress(0, 0)),
                mutableListOf(null, CharAddress(0, 1), null),
                mutableListOf(CharAddress(0, 2), null, null),
                mutableListOf(null, null, null)
            )
        )
    }

    // | F | . | . |
    // | C | D | E |
    private val persistOnFilledUpValues = listOf(
        Pair(Point(0, 0), CharAddress(1, 0)),
        Pair(Point(1, 0), CharAddress(1, 1)),
        Pair(Point(2, 0), CharAddress(1, 2)),
        Pair(Point(0, 1), CharAddress(2, 0))
    )

    @Test
    fun persistOnFilled_Up_slideRight() {
        testPlaceWord(
            w = 3,
            values = persistOnFilledUpValues,
            delta = Pair(Point(0, 1), Pair(UP, "Ab")),
            result = mutableListOf<MutableList<CharAddress?>>(
                mutableListOf(CharAddress(1, 0), CharAddress(1, 1), CharAddress(1, 2)),
                mutableListOf(CharAddress(0, 0), CharAddress(2, 0), null),
                mutableListOf(CharAddress(0, 1), null, null),
                mutableListOf(null, null, null)
            )
        )
    }

    @Test
    fun persistFilled_Right_slideRight() {
        testPlaceWord(
            w = 3,
            values = persistOnFilledUpValues,
            delta = Pair(Point(0, 1), Pair(RIGHT, "Ab")),
            result = mutableListOf<MutableList<CharAddress?>>(
                mutableListOf(CharAddress(1, 0), CharAddress(1, 1), CharAddress(1, 2)),
                mutableListOf(CharAddress(0, 0), CharAddress(0, 1), CharAddress(2, 0)),
                mutableListOf(null, null, null)
            )
        )
    }

    @Test
    fun persistFilled_Leftt_slideRight() {
        testPlaceWord(
            w = 3,
            values = persistOnFilledUpValues,
            delta = Pair(Point(1, 1), Pair(LEFT, "Ab")),
            result = mutableListOf<MutableList<CharAddress?>>(
                mutableListOf(CharAddress(1, 0), CharAddress(1, 1), CharAddress(1, 2)),
                mutableListOf(CharAddress(0, 1), CharAddress(0, 0), CharAddress(2, 0)),
                mutableListOf(null, null, null)
            )
        )
    }

    @Test
    fun persistOnFilled_Left_SlideUp() {
        testPlaceWord(
            w = 3,
            values = persistOnFilledUpValues,
            delta = Pair(Point(2, 0), Pair(LEFT, "Ab")),
            result = mutableListOf<MutableList<CharAddress?>>(
                mutableListOf(CharAddress(1, 0), CharAddress(0, 1), CharAddress(0, 0)),
                mutableListOf(CharAddress(2, 0), CharAddress(1, 1), CharAddress(1, 2)),
                mutableListOf(null, null, null)
            )
        )
    }

    @Test
    fun persistOnFilled_Down_SlideUp() {
        testPlaceWord(
            w = 3,
            values = persistOnFilledUpValues,
            delta = Pair(Point(1, 1), Pair(DOWN, "Ab")),
            result = mutableListOf<MutableList<CharAddress?>>(
                mutableListOf(CharAddress(1, 0), CharAddress(0, 1), CharAddress(1, 2)),
                mutableListOf(CharAddress(2, 0), CharAddress(0, 0), null),
                mutableListOf(null, CharAddress(1, 1), null),
                mutableListOf(null, null, null)
            )
        )
    }

    @Test
    fun persistOnFilled_Up_SlideUp() {
        testPlaceWord(
            w = 3,
            values = persistOnFilledUpValues,
            delta = Pair(Point(0, 0), Pair(UP, "Ab")),
            result = mutableListOf<MutableList<CharAddress?>>(
                mutableListOf(CharAddress(0, 0), CharAddress(1, 1), CharAddress(1, 2)),
                mutableListOf(CharAddress(0, 1), null, null),
                mutableListOf(CharAddress(1, 0), null, null),
                mutableListOf(CharAddress(2, 0), null, null),
                mutableListOf(null, null, null)
            )
        )
    }

    // | E | D |   |   |   |
    // | F | G | H |   |   |
    private val diagValues = listOf(
        Pair(Point(0, 0), CharAddress(2, 0)),
        Pair(Point(1, 0), CharAddress(2, 1)),
        Pair(Point(2, 0), CharAddress(2, 2)),
        Pair(Point(0, 1), CharAddress(1, 1)),
        Pair(Point(1, 1), CharAddress(1, 0))
    )

    @Test
    fun persistOnFilled_UpLeft_slideUp() {
        testPlaceWord(
            w = 5,
            values = diagValues,
            delta = Pair(Point(2, 0), Pair(UP_LEFT, "Abc")),
            result = mutableListOf<MutableList<CharAddress?>>(
                mutableListOf(CharAddress(2, 0), CharAddress(2, 1), CharAddress(0, 0), null, null),
                mutableListOf(CharAddress(1, 1), CharAddress(0, 1), CharAddress(2, 2), null, null),
                mutableListOf(CharAddress(0, 2), CharAddress(1, 0), null, null, null),
                mutableListOf(null, null, null, null, null)
            )
        )
    }

    @Test
    fun persistOnFilled_DownRight_slideRight() {
        testPlaceWord(
            w = 5,
            values = diagValues,
            delta = Pair(Point(0, 2), Pair(DOWN_RIGHT, "Abc")),
            result = mutableListOf<MutableList<CharAddress?>>(
                mutableListOf(CharAddress(2, 0), CharAddress(2, 1), CharAddress(0, 2), null, null),
                mutableListOf(CharAddress(1, 1), CharAddress(0, 1), CharAddress(2, 2), null, null),
                mutableListOf(CharAddress(0, 0), CharAddress(1, 0), null, null, null),
                mutableListOf(null, null, null, null, null)
            )
        )
    }

    @Test
    fun persistOnFilled_LeftFromBottomRight_SlideUp() {
        testPlaceWord(
            w = 5,
            values = diagValues,
            delta = Pair(Point(4, 0), Pair(LEFT, "Abc")),
            result = mutableListOf<MutableList<CharAddress?>>(
                mutableListOf(
                    CharAddress(2, 0),
                    CharAddress(2, 1),
                    CharAddress(0, 2),
                    CharAddress(0, 1),
                    CharAddress(0, 0)
                ),
                mutableListOf(CharAddress(1, 1), CharAddress(1, 0), CharAddress(2, 2), null, null),
                mutableListOf(null, null, null, null, null)
            )
        )
    }

    private fun testPlaceWord(
        w: Int,
        values: List<Pair<Point, CharAddress>> = listOf(),
        delta: Pair<Point, Pair<Point, String>>,
        result: MutableList<MutableList<CharAddress?>>
    ) {
        val grid =
            MutableGrid<CharAddress>(w)
        for (v in values) {
            grid.set(v.first.x, v.first.y, v.second)
        }
        val word = Word.fromValue(delta.second.second, 0, false)
        grid.placeWord(delta.first, delta.second.first, word, gravity)
        assertThat(grid).isEqualTo(
            MutableGrid(
                w,
                result
            )
        )
    }
}
