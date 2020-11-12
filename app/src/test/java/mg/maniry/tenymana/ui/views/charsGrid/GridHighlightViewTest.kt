package mg.maniry.tenymana.ui.views.charsGrid

import android.graphics.Canvas
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.*
import mg.maniry.tenymana.gameLogic.models.Grid
import mg.maniry.tenymana.gameLogic.models.Point
import mg.maniry.tenymana.ui.views.settings.DrawingSettings
import mg.maniry.tenymana.ui.views.charsGrid.BaseCharGridControl.Companion.MARGIN
import mg.maniry.tenymana.utils.TestRect
import mg.maniry.tenymana.utils.TestTextShape
import mg.maniry.tenymana.utils.chars
import org.junit.Test

class GridHighlightViewTest {
    @Test
    fun simpleAnim() {
        // canvas
        // Add value and draw
        val rects = mutableListOf<TestRect>()
        val texts = mutableListOf<TestTextShape>()
        val canvas: Canvas = mock {
            on { drawRect(any(), any(), any(), any(), any()) } doAnswer {
                rects.add(TestRect.fromMock(it.arguments)); Unit
            }
            on { drawText(any(), any(), any(), any()) } doAnswer {
                texts.add(TestTextShape.fromMock(it.arguments)); Unit
            }
        }

        fun GridHighlightControl.reRender(t: Long) {
            val should = onTick(t)
            if (should) {
                onDraw(canvas)
            }
        }
        // control
        val cellSize = 20f
        val textDY = cellSize - MARGIN * 4
        val textDX = (cellSize - MARGIN) / 2
        val x0 = 10f
        val y0 = 100f
        val control = GridHighlightControl()
        control.animDuration = 500.0
        control.settings = DrawingSettings()
            .apply {
            charGridCellSize = cellSize
            charGridOrigin = Point(x0.toInt(), y0.toInt())
        }
        control.onGridChanged(Grid(listOf(chars('A', 'B'), chars('C', 'D'))))
        // add value and ticks
        control.onValue(listOf(Point(0, 0), Point(1, 1)), 1000)
        control.reRender(1000)
        assertThat(rects).isEmpty()
        assertThat(texts).isEmpty()
        // t = 0.1: draw cells[0] = 4, cells[1] = 0
        control.reRender(1050)
        assertThat(rects).isEqualTo(listOf(TestRect.xywh(20f - 2, 90f - 2, 4f, 4f)))
        assertThat(texts).isEqualTo(listOf(TestTextShape("A", 10f + textDX, 82f + textDY)))
        // t = 0.25: cells[0] = 10
        rects.removeAll { true }
        texts.removeAll { true }
        control.reRender(1125)
        assertThat(rects).isEqualTo(listOf(TestRect.xywh(20f - 5, 90f - 5, 10f, 10f)))
        assertThat(texts).isEqualTo(listOf(TestTextShape("A", 10f + textDX, 82f + textDY)))
        // t = 0.3: cells[0] = 12; cells[1] = 4
        rects.removeAll { true }
        texts.removeAll { true }
        control.reRender(1150)
        assertThat(rects).isEqualTo(
            listOf(
                TestRect.xywh(20f - 6, 90f - 6, 12f, 12f),
                TestRect.xywh(40f - 2, 70f - 2, 4f, 4f)
            )
        )
        assertThat(texts).isEqualTo(
            listOf(
                TestTextShape("A", 10f + textDX, 82f + textDY),
                TestTextShape("D", 30f + textDX, 62f + textDY)
            )
        )
        rects.removeAll { true }
        // t = 0.4: cells[0] = 16, cells[1] = 12
        control.reRender(1200)
        assertThat(rects).isEqualTo(
            listOf(
                TestRect.xywh(20f - 8, 90f - 8, 16f, 16f),
                TestRect.xywh(40f - 6, 70f - 6, 12f, 12f)
            )
        )
        rects.removeAll { true }
        // t = 0.5: cells[0] = 20, cells[1] = 20
        val filledRects = listOf(
            TestRect.xywh(20f - 10, 90f - 10, 20f, 20f),
            TestRect.xywh(40f - 10, 70f - 10, 20f, 20f)
        )
        control.reRender(1250)
        assertThat(rects).isEqualTo(filledRects)
        // onTick draws same shape
        clearInvocations(canvas)
        rects.removeAll { true }
        control.reRender(1300)
        control.reRender(1400)
        assertThat(rects).isEqualTo(
            listOf(
                filledRects[0], filledRects[1],
                filledRects[0], filledRects[1]
            )
        )
        // shrink
        // t=0.9; size = 10
        rects.removeAll { true }
        control.reRender(1450)
        assertThat(rects).isEqualTo(
            listOf(
                TestRect.xywh(20f - 5, 90f - 5, 10f, 10f),
                TestRect.xywh(40f - 5, 70f - 5, 10f, 10f)
            )
        )
        // t=9.5; size = 5
        rects.removeAll { true }
        control.reRender(1475)
        assertThat(rects).isEqualTo(
            listOf(
                TestRect.xywh(20f - 2.5f, 90f - 2.5f, 5f, 5f),
                TestRect.xywh(40f - 2.5f, 70f - 2.5f, 5f, 5f)
            )
        )
        // t=1; size = 0
        clearInvocations(canvas)
        control.reRender(1500)
        verifyZeroInteractions(canvas)
    }
}
