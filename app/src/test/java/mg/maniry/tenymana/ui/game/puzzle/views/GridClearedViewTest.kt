package mg.maniry.tenymana.ui.game.puzzle.views

import android.animation.ValueAnimator
import android.graphics.Canvas
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.*
import mg.maniry.tenymana.gameLogic.models.Point
import mg.maniry.tenymana.utils.TestRect
import mg.maniry.tenymana.utils.verifyNever
import mg.maniry.tenymana.utils.verifyOnce
import org.junit.Test

class GridClearedViewTest {
    @Test
    fun simpleAnim() {
        // canvas
        // Add value and draw
        val rects = mutableListOf<TestRect>()
        val canvas: Canvas = mock {
            on { drawRect(any(), any(), any(), any(), any()) } doAnswer {
                rects.add(TestRect.fromMock(it.arguments)); Unit
            }
        }

        fun GridClearedViewControl.reRender(t: Double) {
            val should = onTick(t)
            if (should) {
                onDraw(canvas)
            }
        }
        // animator
        var isRunning = false
        val animator: ValueAnimator = mock {
            on { this.isRunning } doAnswer { isRunning }
            on { start() } doAnswer {
                isRunning = true; Unit
            }
            on { cancel() } doAnswer {
                isRunning = false; Unit
            }
        }
        // control
        val control = GridClearedViewControl(animator)
        val cellSize = 20f
        val x0 = 10f
        val y0 = 100f
        control.settings = DrawingSettings().apply {
            charGridCellSize = cellSize
            charGridOrigin = Point(x0.toInt(), y0.toInt())
        }
        // on null value does nothing
        control.onValue(null)
        verifyNever(animator).cancel()
        verifyNever(animator).start()
        // positive value
        control.onValue(listOf(Point(0, 0), Point(1, 1)))
        verifyNever(animator).cancel()
        verifyOnce(animator).start()
        // positive again stops and then starts
        clearInvocations(animator)
        control.onValue(listOf(Point(0, 0), Point(1, 1)))
        verifyOnce(animator).cancel()
        verifyOnce(animator).start()
        // Null stops
        clearInvocations(animator)
        control.onValue(null)
        verifyOnce(animator).cancel()
        verifyNever(animator).start()

        control.onValue(listOf(Point(0, 0), Point(1, 1)))
        control.apply {
            onTick(0.0)
            onDraw(canvas)
        }
        assertThat(rects).isEmpty()
        // t = 0.1: draw cells[0] = 4, cells[1] = 0
        control.reRender(0.1)
        assertThat(rects).isEqualTo(listOf(TestRect.xywh(20f - 2, 90f - 2, 4f, 4f)))
        // t = 0.25: cells[0] = 10
        rects.removeAll { true }
        control.reRender(0.25)
        assertThat(rects).isEqualTo(listOf(TestRect.xywh(20f - 5, 90f - 5, 10f, 10f)))
        // t = 0.3: cells[0] = 12; cells[1] = 4
        rects.removeAll { true }
        control.reRender(0.3)
        assertThat(rects).isEqualTo(
            listOf(
                TestRect.xywh(20f - 6, 90f - 6, 12f, 12f),
                TestRect.xywh(40f - 2, 70f - 2, 4f, 4f)
            )
        )
        rects.removeAll { true }
        // t = 0.4: cells[0] = 16, cells[1] = 12
        control.reRender(0.4)
        assertThat(rects).isEqualTo(
            listOf(
                TestRect.xywh(20f - 8, 90f - 8, 16f, 16f),
                TestRect.xywh(40f - 6, 70f - 6, 12f, 12f)
            )
        )
        rects.removeAll { true }
        // t = 0.5: cells[0] = 20, cells[1] = 20
        control.reRender(0.5)
        assertThat(rects).isEqualTo(
            listOf(
                TestRect.xywh(20f - 10, 90f - 10, 20f, 20f),
                TestRect.xywh(40f - 10, 70f - 10, 20f, 20f)
            )
        )
        // onTick till 0.8 does nothing
        clearInvocations(canvas)
        rects.removeAll { true }
        control.reRender(0.6)
        control.reRender(0.8)
        verifyZeroInteractions(canvas)
        // shrink
        // t=0.9; size = 10
        control.reRender(0.9)
        assertThat(rects).isEqualTo(
            listOf(
                TestRect.xywh(20f - 5, 90f - 5, 10f, 10f),
                TestRect.xywh(40f - 5, 70f - 5, 10f, 10f)
            )
        )
        // t=95; size = 5
        rects.removeAll { true }
        control.reRender(0.95)
        assertThat(rects).isEqualTo(
            listOf(
                TestRect.xywh(20f - 2.5f, 90f - 2.5f, 5f, 5f),
                TestRect.xywh(40f - 2.5f, 70f - 2.5f, 5f, 5f)
            )
        )
        // t=1; size = 0
        clearInvocations(canvas)
        control.reRender(1.0)
        verifyZeroInteractions(canvas)
    }
}
