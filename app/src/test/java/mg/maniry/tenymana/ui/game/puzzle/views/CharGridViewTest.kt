package mg.maniry.tenymana.ui.game.puzzle.views

import android.graphics.Canvas
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.mock
import mg.maniry.tenymana.gameLogic.models.Character
import mg.maniry.tenymana.gameLogic.models.Grid
import mg.maniry.tenymana.ui.game.puzzle.views.CharGridViewBrain.Companion.MARGIN
import mg.maniry.tenymana.utils.TestRect
import mg.maniry.tenymana.utils.TestTextShape
import org.junit.Test

class CharGridViewTest {
    private val rectSize = 20f - MARGIN
    private val cellSize = 20f

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
                TestRect.xywh(0f, 100f - MARGIN, rectSize, rectSize), // a
                TestRect.xywh(cellSize, 100f - MARGIN, rectSize, rectSize), // b
                TestRect.xywh(cellSize * 2, 100f - MARGIN, rectSize, rectSize), // c
                TestRect.xywh(0f, 100f - cellSize - MARGIN, rectSize, rectSize), // d
                TestRect.xywh(cellSize, 100f - cellSize - MARGIN, rectSize, rectSize) // e
            ),
            texts = listOf(
                TestTextShape("A", rectSize / 2, 100f - MARGIN),
                TestTextShape("B", cellSize + rectSize / 2, 100f - MARGIN),
                TestTextShape("C", 2 * cellSize + rectSize / 2, 100f - MARGIN),
                TestTextShape("D", rectSize / 2, 100f - cellSize - MARGIN),
                TestTextShape("E", cellSize + rectSize / 2, 100f - cellSize - MARGIN)
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
        val brain = CharGridViewBrain().apply {
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
        brain.draw(canvas)
        assertThat(drawnTexts).isEqualTo(texts)
        assertThat(drawnRects).isEqualTo(rects)
    }

    private fun chars(vararg values: Char?): List<Character?> {
        return values.map {
            it?.let { Character(it.toUpperCase(), it.toLowerCase()) }
        }
    }
}
