package mg.maniry.tenymana.ui.views.charsGrid

import android.graphics.Canvas
import android.graphics.Paint
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.mock
import mg.maniry.tenymana.gameLogic.models.Character
import mg.maniry.tenymana.gameLogic.models.Grid
import mg.maniry.tenymana.ui.views.DrawingSettings
import mg.maniry.tenymana.ui.views.charsGrid.BaseCharGridControl.Companion.MARGIN
import mg.maniry.tenymana.ui.views.charsGrid.BaseCharGridControl.Companion.emptyBgPaint
import mg.maniry.tenymana.utils.TestRect
import mg.maniry.tenymana.utils.TestTextShape
import org.junit.Test

class CharGridControlTest {
    private val rectSize = 20f - MARGIN
    private val cellSize = 20f
    private val textDX = (cellSize - MARGIN) / 2
    private val textDY = cellSize - MARGIN * 4

    @Test
    fun draw_contraintX() {
        testDraw(
            grid = listOf(
                chars('a', 'b', 'c', null, null),
                chars('d', 'e', null, null, null),
                chars(null, null, null, null, null)
            ),
            visibleH = 4,
            width = 100,
            height = 100,
            rects = listOf(
                TestRect.xywh(0f, 100f - cellSize - MARGIN, rectSize, rectSize), // a
                TestRect.xywh(cellSize, 100f - cellSize - MARGIN, rectSize, rectSize), // b
                TestRect.xywh(cellSize * 2, 100f - cellSize - MARGIN, rectSize, rectSize), // c
                TestRect.xywh(0f, 100f - 2 * cellSize - MARGIN, rectSize, rectSize), // d
                TestRect.xywh(cellSize, 100f - 2 * cellSize - MARGIN, rectSize, rectSize) // e
            ),
            emptyRects = listOf(
                TestRect.xywh(cellSize * 3, 100f - cellSize - MARGIN, rectSize, rectSize), // 0
                TestRect.xywh(cellSize * 4, 100f - cellSize - MARGIN, rectSize, rectSize),
                TestRect.xywh(cellSize * 2, 100f - 2 * cellSize - MARGIN, rectSize, rectSize), // 1
                TestRect.xywh(cellSize * 3, 100f - 2 * cellSize - MARGIN, rectSize, rectSize),
                TestRect.xywh(cellSize * 4, 100f - 2 * cellSize - MARGIN, rectSize, rectSize),
                TestRect.xywh(cellSize * 0, 100f - 3 * cellSize - MARGIN, rectSize, rectSize), // 2
                TestRect.xywh(cellSize * 1, 100f - 3 * cellSize - MARGIN, rectSize, rectSize),
                TestRect.xywh(cellSize * 2, 100f - 3 * cellSize - MARGIN, rectSize, rectSize),
                TestRect.xywh(cellSize * 3, 100f - 3 * cellSize - MARGIN, rectSize, rectSize),
                TestRect.xywh(cellSize * 4, 100f - 3 * cellSize - MARGIN, rectSize, rectSize),
                TestRect.xywh(cellSize * 0, 100f - 4 * cellSize - MARGIN, rectSize, rectSize), // 3
                TestRect.xywh(cellSize * 1, 100f - 4 * cellSize - MARGIN, rectSize, rectSize),
                TestRect.xywh(cellSize * 2, 100f - 4 * cellSize - MARGIN, rectSize, rectSize),
                TestRect.xywh(cellSize * 3, 100f - 4 * cellSize - MARGIN, rectSize, rectSize),
                TestRect.xywh(cellSize * 4, 100f - 4 * cellSize - MARGIN, rectSize, rectSize)
            ),
            texts = listOf(
                TestTextShape("A", textDX, 100f - cellSize - MARGIN + textDY),
                TestTextShape("B", cellSize + textDX, 100f - cellSize - MARGIN + textDY),
                TestTextShape("C", 2 * cellSize + textDX, 100f - cellSize - MARGIN + textDY),
                TestTextShape("D", textDX, 100f - 2 * cellSize - MARGIN + textDY),
                TestTextShape("E", cellSize + textDX, 100f - 2 * cellSize - MARGIN + textDY)
            )
        )
    }

    @Test
    fun drawUntilVisibleH() {
        testDraw(
            grid = listOf(
                chars('a', null),
                chars('b', null),
                chars('c', null)
            ),
            visibleH = 2,
            width = 40,
            height = 50,
            rects = listOf(
                TestRect.xywh(0f, 50f - cellSize - MARGIN, rectSize, rectSize),
                TestRect.xywh(0f, 50f - 2 * cellSize - MARGIN, rectSize, rectSize)
            ),
            emptyRects = listOf(
                TestRect.xywh(cellSize, 50f - cellSize - MARGIN, rectSize, rectSize),
                TestRect.xywh(cellSize, 50f - 2 * cellSize - MARGIN, rectSize, rectSize)
            ),
            texts = listOf(
                TestTextShape("A", textDX, 50f - cellSize - MARGIN + textDY),
                TestTextShape("B", textDX, 50f - 2 * cellSize - MARGIN + textDY)
            )
        )
    }

    @Test
    fun draw_constraintY() {
        testDraw(
            grid = listOf(
                chars('a', null, null),
                chars('d', null, null)
            ),
            visibleH = 5,
            width = 200,
            height = 102,
            rects = listOf(
                TestRect.xywh(70f, 102f - cellSize - MARGIN, rectSize, rectSize), // a
                TestRect.xywh(70f, 102f - 2 * cellSize - MARGIN, rectSize, rectSize) // d
            ),
            emptyRects = listOf(
                // 0
                TestRect.xywh(cellSize * 1 + 70, 102f - cellSize * 1 - MARGIN, rectSize, rectSize),
                TestRect.xywh(cellSize * 2 + 70, 102f - cellSize * 1 - MARGIN, rectSize, rectSize),
                // 1
                TestRect.xywh(cellSize * 1 + 70, 102f - cellSize * 2 - MARGIN, rectSize, rectSize),
                TestRect.xywh(cellSize * 2 + 70, 102f - cellSize * 2 - MARGIN, rectSize, rectSize),
                // 2
                TestRect.xywh(cellSize * 0 + 70, 102f - cellSize * 3 - MARGIN, rectSize, rectSize),
                TestRect.xywh(cellSize * 1 + 70, 102f - cellSize * 3 - MARGIN, rectSize, rectSize),
                TestRect.xywh(cellSize * 2 + 70, 102f - cellSize * 3 - MARGIN, rectSize, rectSize),
                // 3
                TestRect.xywh(cellSize * 0 + 70, 102f - cellSize * 4 - MARGIN, rectSize, rectSize),
                TestRect.xywh(cellSize * 1 + 70, 102f - cellSize * 4 - MARGIN, rectSize, rectSize),
                TestRect.xywh(cellSize * 2 + 70, 102f - cellSize * 4 - MARGIN, rectSize, rectSize),
                // 4
                TestRect.xywh(cellSize * 0 + 70, 102f - cellSize * 5 - MARGIN, rectSize, rectSize),
                TestRect.xywh(cellSize * 1 + 70, 102f - cellSize * 5 - MARGIN, rectSize, rectSize),
                TestRect.xywh(cellSize * 2 + 70, 102f - cellSize * 5 - MARGIN, rectSize, rectSize)
            ),
            texts = listOf(
                TestTextShape("A", 70f + textDX, 102f - cellSize - MARGIN + textDY),
                TestTextShape("D", 70f + textDX, 102f - 2 * cellSize - MARGIN + textDY)
            )
        )
    }

    private fun testDraw(
        grid: List<List<Character?>>,
        width: Int,
        height: Int,
        visibleH: Int,
        texts: List<TestTextShape>,
        rects: List<TestRect>,
        emptyRects: List<TestRect>
    ) {
        val control = CharGridViewControl().apply {
            settings = DrawingSettings()
            onGridChanged(Grid(grid))
            onSizeChanged(width, height)
            onVisibleHChanged(visibleH)
        }
        val drawnTexts = mutableListOf<TestTextShape>()
        val drawnRects = mutableListOf<TestRect>()
        val drawnEmptyRects = mutableListOf<TestRect>()
        val canvas: Canvas = mock {
            on { drawText(any(), any(), any(), any()) } doAnswer {
                drawnTexts.add(TestTextShape.fromMock(it.arguments))
                Unit
            }
            on { drawRect(any(), any(), any(), any(), any()) } doAnswer {
                val paint = it.arguments[4] as Paint
                val rect = TestRect.fromMock(it.arguments)
                if (paint == emptyBgPaint) {
                    drawnEmptyRects.add(rect)
                } else {
                    drawnRects.add(rect)
                }
                Unit
            }
        }
        // draw backgrounds
        control.draw(canvas)
        assertThat(drawnRects).isEqualTo(rects)
        assertThat(drawnEmptyRects).isEqualTo(emptyRects)
        assertThat(drawnTexts).isEqualTo(texts)
    }

    private fun chars(vararg values: Char?): List<Character?> {
        return values.map {
            it?.let { Character(it.toUpperCase(), it.toLowerCase()) }
        }
    }
}
