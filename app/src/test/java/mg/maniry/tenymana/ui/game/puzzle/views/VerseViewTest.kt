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
        val control = VerseViewControl()
        control.settings = DrawingSettings()
        assertThat(control.settings?.verseViewHeight).isEqualTo(VerseViewControl.SPACING_V)
        // One row
        val words = listOf(
            Word.fromValue("Abc", 0), // 12 * 3 + 2 * 2 = 40
            Word.fromValue(", ", 1, true), // 12 * 2 = 24
            Word.fromValue("de", 2), // 12 * 2 + 2 = 26
            Word.fromValue(" ", 3, true), // 12
            Word.fromValue("fgh", 4) // 12 * 3 + 2 * 2 = 40
        )
        control.onMeasure(90)
        assertThat(control.settings?.verseViewHeight).isEqualTo(VerseViewControl.SPACING_V)
        control.onWordsChange(words)
        assertThat(control.settings?.verseViewHeight)
            .isEqualTo(2 * (VerseViewControl.H + VerseViewControl.SPACING_V) + VerseViewControl.SPACING_V)
    }

    private val y0 = VerseViewControl.SPACING_V.toFloat()
    private val w = VerseViewControl.W.toFloat()
    private val h = VerseViewControl.H.toFloat()
    private val hPlusMrg = h + VerseViewControl.SPACING_V
    private val wPlusMrg = w + VerseViewControl.SPACING_H.toFloat()
    private val textDx = (VerseViewControl.W + VerseViewControl.SPACING_H).toFloat() / 2
    private val textDy = VerseViewControl.H.toFloat() - VerseViewControl.SPACING_V

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
                TestTextShape("A", 0f + textDx, y0 + textDy),
                TestTextShape("b", wPlusMrg + textDx, y0 + textDy),
                TestTextShape("c", 2 * wPlusMrg + textDx, y0 + textDy)
            )
        )
    }

    @Test
    fun draw_wrap_LeadingSpace_break_initialState() {
        testDraw(
            width = 76, // 12*2+2 + 12*2 + 12*2+2
            words = listOf(
                Word.fromValue("ab", 0),
                Word.fromValue(", ", 1, true),
                Word.fromValue("cd", 2),
                Word.fromValue(" ", 3, true),
                Word.fromValue("efghijkl", 4)
            ),
            texts = listOf(
                TestTextShape(",", wPlusMrg + w + textDx, y0 + textDy),
                TestTextShape(" ", wPlusMrg + w + w + textDx, y0 + textDy)
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
            width = 76, // 12*2+2 + 12*2 + 12*2+2
            words = listOf(
                Word.fromValue("ab", 0).resolvedVersion,
                Word.fromValue(", ", 1, true),
                Word.fromValue("efghijkl", 2).resolvedVersion,
                Word.fromValue(" ", 3, true),
                Word.fromValue("cd", 4).resolvedVersion
            ),
            texts = listOf(
                TestTextShape("a", 0f + textDx, y0 + textDy),
                TestTextShape("b", wPlusMrg + textDx, y0 + textDy),
                TestTextShape(",", wPlusMrg + w + textDx, y0 + textDy),
                TestTextShape(" ", wPlusMrg + w + w + textDx, y0 + textDy),
                TestTextShape("e", 0f + textDx, hPlusMrg + y0 + textDy),
                TestTextShape("f", wPlusMrg + textDx, hPlusMrg + y0 + textDy),
                TestTextShape("g", 2 * wPlusMrg + textDx, hPlusMrg + y0 + textDy),
                TestTextShape("h", 3 * wPlusMrg + textDx, hPlusMrg + y0 + textDy),
                TestTextShape("i", 4 * wPlusMrg + textDx, hPlusMrg + y0 + textDy),
                TestTextShape("j", 0f + textDx, 2 * hPlusMrg + y0 + textDy),
                TestTextShape("k", wPlusMrg + textDx, 2 * hPlusMrg + y0 + textDy),
                TestTextShape("l", 2 * wPlusMrg + textDx, 2 * hPlusMrg + y0 + textDy),
                TestTextShape(" ", 2 * wPlusMrg + w + textDx, 2 * hPlusMrg + y0 + textDy),
                TestTextShape("c", 0f + textDx, 3 * hPlusMrg + y0 + textDy),
                TestTextShape("d", wPlusMrg + textDx, 3 * hPlusMrg + y0 + textDy)
            ),
            rects = emptyList()
        )
    }

    @Test
    fun draw_wrapAtEndOfRow() {
        testDraw(
            width = 28,
            words = listOf(
                Word.fromValue("Abcd", 0).resolvedVersion,
                Word.fromValue("-", 1, true),
                Word.fromValue("ef", 2).resolvedVersion
            ),
            texts = listOf(
                TestTextShape("A", 0f + textDx, y0 + textDy),
                TestTextShape("b", wPlusMrg + textDx, y0 + textDy),
                TestTextShape("c", 0f + textDx, hPlusMrg + y0 + textDy),
                TestTextShape("d", wPlusMrg + textDx, hPlusMrg + y0 + textDy),
                TestTextShape("-", 0f + textDx, 2 * hPlusMrg + y0 + textDy),
                TestTextShape("e", 0f + textDx, 3 * hPlusMrg + y0 + textDy),
                TestTextShape("f", wPlusMrg + textDx, 3 * hPlusMrg + y0 + textDy)
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
        val control = VerseViewControl().apply {
            settings = DrawingSettings()
            onWordsChange(words)
            onMeasure(width)
            onColorsChange(Color.RED, Color.BLUE)
        }
        control.draw(canvas)
        assertThat(resTexts).isEqualTo(texts)
        assertThat(resRects).isEqualTo(rects)
    }
}
