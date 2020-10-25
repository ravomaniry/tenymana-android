package mg.maniry.tenymana.ui.game.puzzle.views

import android.graphics.Canvas
import android.graphics.Color
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.mock
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

    private val y0 = VerseViewBrain.SPACING_V.toFloat()
    private val w = VerseViewBrain.W.toFloat()
    private val h = VerseViewBrain.H.toFloat()
    private val hPlusMrg = h + VerseViewBrain.SPACING_V
    private val wPlusMrg = w + VerseViewBrain.SPACING_H.toFloat()

    @Test
    fun drawSingleRow_initialState() {
        testDraw(
            width = 100,
            words = listOf(Word.fromValue("Abc", 0)),
            texts = listOf(),
            rects = listOf(
                Rect.xywh(0f, y0, w, h),
                Rect.xywh(wPlusMrg, y0, w, h),
                Rect.xywh(2 * wPlusMrg, y0, w, h)
            )
        )
    }

    @Test
    fun drawOneRow_resolved() {
        testDraw(
            width = 100,
            words = listOf(Word.fromValue("Abc", 0).resolvedVersion),
            rects = emptyList(),
            texts = listOf(
                Text("A", 0f, y0),
                Text("b", wPlusMrg, y0),
                Text("c", 2 * wPlusMrg, y0)
            )
        )
    }

    @Test
    fun drawWrap_LeadingSpace_break_initialState() {
        testDraw(
            width = 104,
            words = listOf(
                Word.fromValue("ab", 0),
                Word.fromValue(", ", 1, true),
                Word.fromValue("cd", 2),
                Word.fromValue(" ", 3, true),
                Word.fromValue("efghijkl", 4)
            ),
            texts = listOf(
                Text(",", wPlusMrg + w, y0),
                Text(" ", wPlusMrg + w + w, y0)
            ),
            rects = listOf(
                Rect.xywh(0f, y0, w, h), // a
                Rect.xywh(wPlusMrg, y0, w, h), // b
                Rect.xywh(wPlusMrg + w + 2 * w, y0, w, h), // c
                Rect.xywh(wPlusMrg + w + 2 * w + wPlusMrg, y0, w, h), // d
                Rect.xywh(0f, hPlusMrg + y0, w, h), // e
                Rect.xywh(wPlusMrg, hPlusMrg + y0, w, h), // f
                Rect.xywh(wPlusMrg * 2, hPlusMrg + y0, w, h), // g
                Rect.xywh(wPlusMrg * 3, hPlusMrg + y0, w, h), // h
                Rect.xywh(wPlusMrg * 4, hPlusMrg + y0, w, h), // i
                Rect.xywh(0f, hPlusMrg * 2 + y0, w, h), // j
                Rect.xywh(wPlusMrg, hPlusMrg * 2 + y0, w, h), // k
                Rect.xywh(wPlusMrg * 2, hPlusMrg * 2 + y0, w, h) // l
            )
        )
    }

    @Test
    fun drawWrap_LeadingSpace_break_revealed() {
        testDraw(
            width = 104,
            words = listOf(
                Word.fromValue("ab", 0).resolvedVersion,
                Word.fromValue(", ", 1, true),
                Word.fromValue("cd", 2).resolvedVersion,
                Word.fromValue(" ", 3, true),
                Word.fromValue("efghijkl", 4).resolvedVersion
            ),
            texts = listOf(
                Text("a", 0f, y0),
                Text("b", wPlusMrg, y0),
                Text(",", wPlusMrg + w, y0),
                Text(" ", wPlusMrg + w + w, y0),
                Text("c", wPlusMrg + w + 2 * w, y0),
                Text("d", wPlusMrg + w + 2 * w + wPlusMrg, y0),
                Text("e", 0f, hPlusMrg + y0),
                Text("f", wPlusMrg, hPlusMrg + y0),
                Text("g", 2 * wPlusMrg, hPlusMrg + y0),
                Text("h", 3 * wPlusMrg, hPlusMrg + y0),
                Text("i", 4 * wPlusMrg, hPlusMrg + y0),
                Text("j", 0f, 2 * hPlusMrg + y0),
                Text("k", wPlusMrg, 2 * hPlusMrg + y0),
                Text("l", 2 * wPlusMrg, 2 * hPlusMrg + y0)
            ),
            rects = emptyList()
        )
    }

    private fun testDraw(width: Int, words: List<Word>, texts: List<Text>, rects: List<Rect>) {
        val resRects = mutableListOf<Rect>()
        val resTexts = mutableListOf<Text>()
        val drawRect: (InvocationOnMock) -> Unit = {
            resRects.add(
                Rect(
                    it.arguments[0] as Float,
                    it.arguments[1] as Float,
                    it.arguments[2] as Float,
                    it.arguments[3] as Float
                )
            )
        }
        val drawText: (InvocationOnMock) -> Unit = {
            resTexts.add(
                Text(it.arguments[0] as String, it.arguments[1] as Float, it.arguments[2] as Float)
            )
        }
        val canvas: Canvas = mock {
            on { drawRect(any(), any(), any(), any(), any()) } doAnswer drawRect
            on { drawText(any(), any(), any(), any()) } doAnswer drawText
        }
        val brain = VerseViewBrain()
        brain.onWordsChange(words)
        brain.onMeasure(width)
        brain.onColorsChange(Color.RED, Color.BLUE)
        brain.draw(canvas)
        assertThat(resTexts).isEqualTo(texts)
        assertThat(resRects).isEqualTo(rects)
    }
}

private data class Rect(
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float
) {
    companion object {
        fun xywh(x: Float, y: Float, w: Float, h: Float): Rect {
            return Rect(x, y, x + w, y + h)
        }
    }
}

private data class Text(
    val value: String,
    val x: Float,
    val y: Float
)
