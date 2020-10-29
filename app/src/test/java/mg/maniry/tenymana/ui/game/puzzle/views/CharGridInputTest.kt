package mg.maniry.tenymana.ui.game.puzzle.views

import android.graphics.Canvas
import android.graphics.Color
import android.view.MotionEvent
import android.view.MotionEvent.*
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import mg.maniry.tenymana.gameLogic.models.Character
import mg.maniry.tenymana.gameLogic.models.Grid
import mg.maniry.tenymana.gameLogic.models.Move
import mg.maniry.tenymana.gameLogic.models.Point
import mg.maniry.tenymana.utils.TestLine
import mg.maniry.tenymana.utils.TestMotionEvent
import org.junit.Test

class CharGridInputTest {
    @Test
    fun singleHorizontalMove() {
        testInput(
            gridWidth = 2,
            origin = Point(100, 100),
            cellSize = 20f,
            events = listOf(
                TestMotionEvent(ACTION_DOWN, 110f, 95f),
                TestMotionEvent(ACTION_MOVE, 121f, 94f),
                TestMotionEvent(ACTION_UP, 121f, 94f)
            ),
            lines = listOf(
                TestLine(110f, 95f, 121f, 94f)
            ),
            move = Move.xy(0, 0, 1, 0)
        )
    }

    @Test
    fun simpleVerticalMoves() {
        testInput(
            gridWidth = 3,
            origin = Point(0, 200),
            cellSize = 25f,
            events = listOf(
                TestMotionEvent(ACTION_MOVE, 25f, 155f),
                TestMotionEvent(ACTION_MOVE, 25f, 101f),
                TestMotionEvent(ACTION_MOVE, 28f, 55f),
                TestMotionEvent(ACTION_UP, 28f, 55f)
            ),
            lines = listOf(
                TestLine(25f, 155f, 25f, 101f),
                TestLine(25f, 155f, 28f, 55f)
            ),
            move = Move.xy(1, 1, 1, 5)
        )
    }

    @Test
    fun diagonal_outsideArea() {
        testInput(
            gridWidth = 3,
            origin = Point(50, 100),
            cellSize = 10f,
            events = listOf(
                TestMotionEvent(ACTION_MOVE, 45f, 106f),
                TestMotionEvent(ACTION_MOVE, 92f, 55f),
                TestMotionEvent(ACTION_UP, 92f, 55f)
            ),
            lines = listOf(TestLine(45f, 106f, 92f, 55f)),
            move = Move.xy(-1, -1, 4, 4)
        )
    }

    private fun testInput(
        gridWidth: Int,
        origin: Point,
        cellSize: Float,
        events: List<TestMotionEvent>,
        lines: List<TestLine>,
        move: Move?
    ) {
        val control = CharGridInputControl()
        control.onColorChanged(Color.BLUE)
        // Grid
        val grid: Grid<Character> = mock {
            on { w } doReturn gridWidth
        }
        control.onGridChanged(grid)
        // propose
        var resMove: Move? = null
        control.propose = { resMove = it }
        // settings
        control.settings = DrawingSettings().apply {
            charGridCellSize = cellSize
            charGridOrigin = origin
        }
        // Canvas
        val resLines = mutableListOf<TestLine>()
        val canvas: Canvas = mock {
            on { drawLine(any(), any(), any(), any(), any()) } doAnswer {
                resLines.add(
                    TestLine(
                        it.arguments[0] as Float,
                        it.arguments[1] as Float,
                        it.arguments[2] as Float,
                        it.arguments[3] as Float
                    )
                )
                Unit
            }
        }
        // Events & run
        for (e in events) {
            val event: MotionEvent = mock {
                on { action } doReturn e.action
                on { x } doReturn e.x
                on { y } doReturn e.y
            }
            control.onTouch(event)
            control.draw(canvas)
        }
        assertThat(resLines).isEqualTo(lines)
        assertThat(resMove).isEqualTo(move)
    }
}

