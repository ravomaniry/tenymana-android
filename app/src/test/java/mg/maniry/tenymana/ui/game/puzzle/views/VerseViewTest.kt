package mg.maniry.tenymana.ui.game.puzzle.views

import android.graphics.Canvas
import android.graphics.Color
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.mock
import mg.maniry.tenymana.gameLogic.models.Word
import mg.maniry.tenymana.utils.TestRect
import mg.maniry.tenymana.utils.TestTextShape
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
                TestRect.xywh(0f, y0, w, h),
                TestRect.xywh(wPlusMrg, y0, w, h),
                TestRect.xywh(2 * wPlusMrg, y0, w, h)
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
                TestTextShape("A", 0f + w / 2, y0),
                TestTextShape("b", wPlusMrg + w / 2, y0),
                TestTextShape("c", 2 * wPlusMrg + w / 2, y0)
            )
        )
    }

    @Test
    fun draw_wrap_LeadingSpace_break_initialState() {
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
                TestTextShape(",", wPlusMrg + w + w / 2, y0),
                TestTextShape(" ", wPlusMrg + w + w + w / 2, y0)
            ),
            rects = listOf(
                TestRect.xywh(0f, y0, w, h), // a
                TestRect.xywh(wPlusMrg, y0, w, h), // b
                TestRect.xywh(wPlusMrg + w + 2 * w, y0, w, h), // c
                TestRect.xywh(wPlusMrg + w + 2 * w + wPlusMrg, y0, w, h), // d
                TestRect.xywh(0f, hPlusMrg + y0, w, h), // e
                TestRect.xywh(wPlusMrg, hPlusMrg + y0, w, h), // f
                TestRect.xywh(wPlusMrg * 2, hPlusMrg + y0, w, h), // g
                TestRect.xywh(wPlusMrg * 3, hPlusMrg + y0, w, h), // h
                TestRect.xywh(wPlusMrg * 4, hPlusMrg + y0, w, h), // i
                TestRect.xywh(0f, hPlusMrg * 2 + y0, w, h), // j
                TestRect.xywh(wPlusMrg, hPlusMrg * 2 + y0, w, h), // k
                TestRect.xywh(wPlusMrg * 2, hPlusMrg * 2 + y0, w, h) // l
            )
        )
    }

    @Test
    fun draw_wrap_LeadingSpace_break_revealed_wordsAfterWrap() {
        testDraw(
            width = 104,
            words = listOf(
                Word.fromValue("ab", 0).resolvedVersion,
                Word.fromValue(", ", 1, true),
                Word.fromValue("efghijkl", 2).resolvedVersion,
                Word.fromValue(" ", 3, true),
                Word.fromValue("cd", 4).resolvedVersion
            ),
            texts = listOf(
                TestTextShape("a", 0f + w / 2, y0),
                TestTextShape("b", wPlusMrg + w / 2, y0),
                TestTextShape(",", wPlusMrg + w + w / 2, y0),
                TestTextShape(" ", wPlusMrg + w + w + w / 2, y0),
                TestTextShape("e", 0f + w / 2, hPlusMrg + y0),
                TestTextShape("f", wPlusMrg + w / 2, hPlusMrg + y0),
                TestTextShape("g", 2 * wPlusMrg + w / 2, hPlusMrg + y0),
                TestTextShape("h", 3 * wPlusMrg + w / 2, hPlusMrg + y0),
                TestTextShape("i", 4 * wPlusMrg + w / 2, hPlusMrg + y0),
                TestTextShape("j", 0f + w / 2, 2 * hPlusMrg + y0),
                TestTextShape("k", wPlusMrg + w / 2, 2 * hPlusMrg + y0),
                TestTextShape("l", 2 * wPlusMrg + w / 2, 2 * hPlusMrg + y0),
                TestTextShape(" ", 2 * wPlusMrg + w + w / 2, 2 * hPlusMrg + y0),
                TestTextShape("c", 0f + w / 2, 3 * hPlusMrg + y0),
                TestTextShape("d", wPlusMrg + w / 2, 3 * hPlusMrg + y0)
            ),
            rects = emptyList()
        )
    }

    @Test
    fun draw_wrapAtEndOfRow() {
        testDraw(
            width = 40,
            words = listOf(
                Word.fromValue("Abcd", 0).resolvedVersion,
                Word.fromValue("-", 1, true),
                Word.fromValue("ef", 2).resolvedVersion
            ),
            texts = listOf(
                TestTextShape("A", 0f + w / 2, y0),
                TestTextShape("b", wPlusMrg + w / 2, y0),
                TestTextShape("c", 0f + w / 2, hPlusMrg + y0),
                TestTextShape("d", wPlusMrg + w / 2, hPlusMrg + y0),
                TestTextShape("-", 0f + w / 2, 2 * hPlusMrg + y0),
                TestTextShape("e", 0f + w / 2, 3 * hPlusMrg + y0),
                TestTextShape("f", wPlusMrg + w / 2, 3 * hPlusMrg + y0)
            ),
            rects = emptyList()
        )
    }

    private fun testDraw(
        width: Int,
        words: List<Word>,
        texts: List<TestTextShape>,
        rects: List<TestRect>
    ) {
        val resRects = mutableListOf<TestRect>()
        val resTexts = mutableListOf<TestTextShape>()
        val drawRect: (InvocationOnMock) -> Unit = {
            resRects.add(
                TestRect(
                    it.arguments[0] as Float,
                    it.arguments[1] as Float,
                    it.arguments[2] as Float,
                    it.arguments[3] as Float
                )
            )
        }
        val drawText: (InvocationOnMock) -> Unit = {
            resTexts.add(
                TestTextShape(
                    it.arguments[0] as String,
                    it.arguments[1] as Float,
                    it.arguments[2] as Float
                )
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
