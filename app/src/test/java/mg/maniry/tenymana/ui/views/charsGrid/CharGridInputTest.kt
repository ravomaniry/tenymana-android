package mg.maniry.tenymana.ui.views.charsGrid

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
import mg.maniry.tenymana.ui.views.charsGrid.BaseCharGridControl.Companion.MARGIN
import mg.maniry.tenymana.ui.views.settings.DrawingSettings
import mg.maniry.tenymana.utils.TestMotionEvent
import mg.maniry.tenymana.utils.TestRect
import mg.maniry.tenymana.utils.TestTextShape
import mg.maniry.tenymana.utils.chars
import org.junit.Test

class CharGridInputTest {
    @Test
    fun horizontal_cells_are_filled() {
        val cellSize = 20f
        val cells = listOf(chars('A', 'B'), chars(null, null))
        val textDY = cellSize - MARGIN * 4
        val textDX = (cellSize - MARGIN) / 2
        val aText = TestTextShape("A", textDX + 100f, 82f + textDY)
        val bText = TestTextShape("B", textDX + 120f, 82f + textDY)
        val aRect = TestRect.xywh(100f, 82f, cellSize - MARGIN, cellSize - MARGIN)
        val bRect = TestRect.xywh(120f, 82f, cellSize - MARGIN, cellSize - MARGIN)
        testInput(
            cells = cells,
            origin = Point(100, 100),
            cellSize = cellSize,
            events = listOf(
                TestMotionEvent(ACTION_DOWN, 110f, 95f),
                TestMotionEvent(ACTION_MOVE, 121f, 94f),
                TestMotionEvent(ACTION_UP, 121f, 94f)
            ),
            rects = listOf(aRect, aRect, bRect),
            texts = listOf(aText, aText, bText),
            move = Move.xy(0, 0, 1, 0)
        )
        testInput(
            cells = cells,
            origin = Point(100, 100),
            cellSize = cellSize,
            events = listOf(
                TestMotionEvent(ACTION_DOWN, 121f, 94f),
                TestMotionEvent(ACTION_MOVE, 110f, 95f),
                TestMotionEvent(ACTION_UP, 110f, 95f)
            ),
            rects = listOf(bRect, bRect, aRect),
            texts = listOf(bText, bText, aText),
            move = Move.xy(1, 0, 0, 0)
        )
    }

    @Test
    fun simpleVerticalMoves_one_cell_filled() {
        val cellSize = 25f
        val textDY = cellSize - MARGIN * 4
        val textDX = (cellSize - MARGIN) / 2
        val dRect = TestRect.xywh(25f, 152f, cellSize - MARGIN, cellSize - MARGIN)
        val dText = TestTextShape("D", 25f + textDX, 152f + textDY)
        val fRect = TestRect.xywh(25f, 127f, cellSize - MARGIN, cellSize - MARGIN)
        val fText = TestTextShape("F", 25f + textDX, 127f + textDY)
        val cells = listOf(
            chars('A', 'B', null),
            chars('C', 'D', null),
            chars('E', 'F', null),
            chars('F', null, null),
            chars(null, null, null),
            chars(null, null, null)
        )
        testInput(
            cells = cells,
            origin = Point(0, 200),
            cellSize = cellSize,
            events = listOf(
                TestMotionEvent(ACTION_MOVE, 25f, 155f),
                TestMotionEvent(ACTION_MOVE, 28f, 55f),
                TestMotionEvent(ACTION_UP, 28f, 55f)
            ),
            rects = listOf(dRect, dRect, fRect),
            texts = listOf(dText, dText, fText),
            move = Move.xy(1, 1, 1, 5)
        )
        testInput(
            cells = cells,
            origin = Point(0, 200),
            cellSize = cellSize,
            events = listOf(
                TestMotionEvent(ACTION_MOVE, 28f, 55f),
                TestMotionEvent(ACTION_MOVE, 25f, 155f),
                TestMotionEvent(ACTION_UP, 25f, 155f)
            ),
            rects = listOf(fRect, dRect),
            texts = listOf(fText, dText),
            move = Move.xy(1, 5, 1, 1)
        )
    }

    @Test
    fun diagonal_outsideArea() {
        val cellSize = 10f
        val textDY = cellSize - MARGIN * 4
        val textDX = (cellSize - MARGIN) / 2
        val aRect = TestRect.xywh(50f, 92f, cellSize - MARGIN, cellSize - MARGIN)
        val aText = TestTextShape("A", 50f + textDX, 92f + textDY)
        val dRect = TestRect.xywh(60f, 82f, cellSize - MARGIN, cellSize - MARGIN)
        val dText = TestTextShape("D", 60f + textDX, 82f + textDY)
        val cells = listOf(
            chars('A', 'B', null),
            chars('C', 'D', null),
            chars(null, null, null)
        )
        testInput(
            cells = cells,
            origin = Point(50, 100),
            cellSize = cellSize,
            events = listOf(
                TestMotionEvent(ACTION_MOVE, 45f, 106f), // -
                TestMotionEvent(ACTION_MOVE, 75f, 75f), // a d
                TestMotionEvent(ACTION_UP, 92f, 55f)
            ),
            rects = listOf(aRect, dRect),
            texts = listOf(aText, dText),
            move = Move.xy(-1, -1, 4, 4)
        )
        testInput(
            cells = cells,
            origin = Point(50, 100),
            cellSize = cellSize,
            events = listOf(
                TestMotionEvent(ACTION_MOVE, 92f, 55f), // -
                TestMotionEvent(ACTION_MOVE, 45f, 106f),
                TestMotionEvent(ACTION_UP, 45f, 106f)
            ),
            rects = listOf(dRect, aRect),
            texts = listOf(dText, aText),
            move = Move.xy(4, 4, -1, -1)
        )
    }

    private fun testInput(
        cells: List<List<Character?>>,
        origin: Point,
        cellSize: Float,
        events: List<TestMotionEvent>,
        rects: List<TestRect>,
        texts: List<TestTextShape>,
        move: Move?
    ) {
        val control = CharGridInputControl()
        control.onColorChanged(Color.BLUE)
        // Grid
        val grid = Grid(cells)
        control.onGridChanged(grid)
        // propose
        var resMove: Move? = null
        control.propose = { resMove = it }
        // settings
        control.settings = DrawingSettings()
            .apply {
                charGridCellSize = cellSize
                charGridOrigin = origin
            }
        // Canvas
        val drawnRects = mutableListOf<TestRect>()
        val drawsTexts = mutableListOf<TestTextShape>()
        val canvas: Canvas = mock {
            on { drawRect(any(), any(), any(), any(), any()) } doAnswer {
                drawnRects.add(TestRect.fromMock(it.arguments)); Unit
            }
            on { drawText(any(), any(), any(), any()) } doAnswer {
                drawsTexts.add(TestTextShape.fromMock(it.arguments)); Unit
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
        assertThat(drawnRects).isEqualTo(rects)
        assertThat(drawsTexts).isEqualTo(texts)
        assertThat(resMove).isEqualTo(move)
    }
}

