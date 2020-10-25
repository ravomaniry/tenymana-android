package mg.maniry.tenymana.ui.game.puzzle.views

import android.graphics.Canvas
import android.graphics.Color
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import mg.maniry.tenymana.gameLogic.models.Word
import org.junit.Test
import org.mockito.invocation.InvocationOnMock

class VerseViewTest {
    @Test
    fun measure() {
        // words is null
        val brain = VerseViewBrain()
        assertThat(brain.height).isEqualTo(VerseViewBrain.SPACING_V)
        // One row
        val words = listOf(
            Word.fromValue("Abc", 0), // 16 * 3 + 4 * 2 = 56
            Word.fromValue(", ", 1, true), // 16 * 2 = 32
            Word.fromValue("de", 2), // 16 * 2 + 4 = 36
            Word.fromValue(" ", 3, true), // 16
            Word.fromValue("fgh", 4) // 16 * 3 + 4 * 2 = 56
        )
        brain.onMeasure(124)
        assertThat(brain.height).isEqualTo(VerseViewBrain.SPACING_V)
        brain.onWordsChange(words)
        assertThat(brain.height)
            .isEqualTo(2 * (VerseViewBrain.H + VerseViewBrain.SPACING_V) + VerseViewBrain.SPACING_V)
    }

    @Test
    fun draw() {
        data class Rect(
            val left: Float,
            val top: Float,
            val right: Float,
            val bottom: Float
        )

        data class Text(
            val value: String,
            val x: Float,
            val y: Float
        )

        val rects = mutableListOf<Rect>()
        val texts = mutableListOf<Text>()
        val drawRect: (InvocationOnMock) -> Unit = {
            rects.add(
                Rect(
                    it.arguments[0] as Float,
                    it.arguments[1] as Float,
                    it.arguments[2] as Float,
                    it.arguments[3] as Float
                )
            )
        }
        val drawText: (InvocationOnMock) -> Unit = {
            texts.add(
                Text(it.arguments[0] as String, it.arguments[1] as Float, it.arguments[2] as Float)
            )
        }
        val canvas: Canvas = mock {
            on { drawRoundRect(any(), any(), any(), any(), any(), any(), any()) } doAnswer drawRect
            on { drawRect(any(), any(), any(), any(), any()) } doAnswer drawRect
            on { drawText(any(), any(), any(), any()) } doAnswer drawText
        }
        val brain = VerseViewBrain()
        brain.draw(canvas, 22)
        verifyZeroInteractions(canvas)
        // One row
        val words0 = listOf(Word.fromValue("Abc", 0))
        brain.onWordsChange(words0)
        brain.onColorsChange(Color.RED, Color.BLUE)
        brain.onMeasure(100)
        brain.draw(canvas, 22)
        val y0 = VerseViewBrain.SPACING_V.toFloat()
        val w = VerseViewBrain.W.toFloat()
        val h = VerseViewBrain.H.toFloat()
        val maringH = VerseViewBrain.SPACING_H.toFloat()
        assertThat(rects).isEqualTo(
            mutableListOf(
                Rect(0f, y0, w, y0 + h),
                Rect(w + maringH, y0, w + maringH + w, y0 + h),
                Rect(2 * (w + maringH), y0, 2 * (w + maringH) + w, y0 + h)
            )
        )
        assertThat(texts).isEmpty()
        // revealed
        rects.removeAll { true }
        val words1 = listOf(words0[0].resolvedVersion)
        brain.onWordsChange(words1)
        brain.draw(canvas, 22)
        assertThat(rects).isEmpty()
        assertThat(texts).isEqualTo(
            mutableListOf(
                Text("A", 0f, y0),
                Text("b", w + maringH, y0),
                Text("c", 2 * (w + maringH), y0)
            )
        )
    }
}
