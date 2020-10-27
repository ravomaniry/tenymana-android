package mg.maniry.tenymana.ui.game.puzzle.views

import android.graphics.Canvas
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.mock
import mg.maniry.tenymana.gameLogic.models.Character
import mg.maniry.tenymana.gameLogic.models.Grid
import mg.maniry.tenymana.ui.game.puzzle.views.CharGridViewControl.Companion.MARGIN
import mg.maniry.tenymana.utils.TestRect
import mg.maniry.tenymana.utils.TestTextShape
import org.junit.Test

class CharGridViewTest {
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
                chars(null, null, null, null)
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
    fun draw_contraintY() {
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
        rects: List<TestRect>
    ) {
        val control = CharGridViewControl().apply {
            settings = DrawingSettings()
            onGridChanged(Grid(grid))
            onSizeChanged(width, height)
            onVisibleHChanged(visibleH)
        }
        val drawnTexts = mutableListOf<TestTextShape>()
        val drawnRects = mutableListOf<TestRect>()
        val canvas: Canvas = mock {
            on { drawText(any(), any(), any(), any()) } doAnswer {
                drawnTexts.add(
                    TestTextShape(
                        it.arguments[0] as String,
                        it.arguments[1] as Float,
                        it.arguments[2] as Float
                    )
                )
                Unit
            }
            on { drawRect(any(), any(), any(), any(), any()) } doAnswer {
                drawnRects.add(
                    TestRect(
                        it.arguments[0] as Float,
                        it.arguments[1] as Float,
                        it.arguments[2] as Float,
                        it.arguments[3] as Float
                    )
                )
                Unit
            }
        }
        control.draw(canvas)
        assertThat(drawnTexts).isEqualTo(texts)
        assertThat(drawnRects).isEqualTo(rects)
    }

    private fun chars(vararg values: Char?): List<Character?> {
        return values.map {
            it?.let { Character(it.toUpperCase(), it.toLowerCase()) }
        }
    }
}
