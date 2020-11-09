package mg.maniry.tenymana.ui.views.verse

import android.graphics.Canvas
import android.graphics.Color
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.mock
import mg.maniry.tenymana.gameLogic.models.Word
import mg.maniry.tenymana.ui.views.DrawingSettings
import mg.maniry.tenymana.ui.views.verse.VerseViewControl.Companion.H
import mg.maniry.tenymana.ui.views.verse.VerseViewControl.Companion.PADDING
import mg.maniry.tenymana.ui.views.verse.VerseViewControl.Companion.SPACING_H
import mg.maniry.tenymana.ui.views.verse.VerseViewControl.Companion.SPACING_V
import mg.maniry.tenymana.ui.views.verse.VerseViewControl.Companion.W
import mg.maniry.tenymana.utils.TestRect
import mg.maniry.tenymana.utils.TestTextShape
import org.junit.Test
import org.mockito.invocation.InvocationOnMock

class VerseViewControlTest {
    @Test
    fun measure() {
        // words is null
        val control = VerseViewControl()
        control.settings = DrawingSettings()
        assertThat(control.settings?.verseViewHeight).isEqualTo(2 * PADDING)
        // One row
        val words = listOf(
            Word.fromValue("Abc", 0), // 12 * 3 + 2 * 2 = 40
            Word.fromValue(", ", 1, true), // 12 * 2 = 24
            Word.fromValue("de", 2), // 12 * 2 + 2 = 26
            Word.fromValue(" ", 3, true), // 12
            Word.fromValue("fgh", 4) // 12 * 3 + 2 * 2 = 40
        )
        control.onMeasure(100)
        assertThat(control.settings?.verseViewHeight).isEqualTo(PADDING * 2)
        control.onWordsChange(words)
        assertThat(control.settings?.verseViewHeight).isEqualTo(2 * H + SPACING_V + 2 * PADDING)
    }

    private val x0 = PADDING.toFloat()
    private val y0 = PADDING.toFloat()
    private val w = W.toFloat()
    private val h = H.toFloat()
    private val hPlusMrg = h + SPACING_V
    private val wPlusMrg = w + SPACING_H
    private val textDx = (W + SPACING_H).toFloat() / 2
    private val textDy = H.toFloat() - SPACING_V

    @Test
    fun drawSingleRow_initialState() {
        testDraw(
            width = 110,
            words = listOf(Word.fromValue("Abc", 0)),
            texts = listOf(),
            rects = listOf(
                TestRect.xywh(x0 + 0f, y0, w, h),
                TestRect.xywh(x0 + wPlusMrg, y0, w, h),
                TestRect.xywh(x0 + 2 * wPlusMrg, y0, w, h)
            )
        )
    }

    @Test
    fun drawOneRow_resolved() {
        testDraw(
            width = 110,
            words = listOf(Word.fromValue("Abc", 0).resolvedVersion),
            rects = emptyList(),
            texts = listOf(
                TestTextShape("A", x0 + 0f + textDx, y0 + textDy),
                TestTextShape("b", x0 + wPlusMrg + textDx, y0 + textDy),
                TestTextShape("c", x0 + 2 * wPlusMrg + textDx, y0 + textDy)
            )
        )
    }

    @Test
    fun draw_wrap_LeadingSpace_break_initialState() {
        testDraw(
            width = 86, // 12*2+2 + 12*2 + 12*2+2 + 5*2
            words = listOf(
                Word.fromValue("ab", 0),
                Word.fromValue(", ", 1, true),
                Word.fromValue("cd", 2),
                Word.fromValue(" ", 3, true),
                Word.fromValue("efghijkl", 4)
            ),
            texts = listOf(
                TestTextShape(",", x0 + wPlusMrg + w + textDx, y0 + textDy),
                TestTextShape(" ", x0 + wPlusMrg + w + w + textDx, y0 + textDy)
            ),
            rects = listOf(
                TestRect.xywh(x0 + 0f, y0, w, h), // a
                TestRect.xywh(x0 + wPlusMrg, y0, w, h), // b
                TestRect.xywh(x0 + wPlusMrg + w + 2 * w, y0, w, h), // c
                TestRect.xywh(x0 + wPlusMrg + w + 2 * w + wPlusMrg, y0, w, h), // d
                TestRect.xywh(x0 + 0f, hPlusMrg + y0, w, h), // e
                TestRect.xywh(x0 + wPlusMrg, hPlusMrg + y0, w, h), // f
                TestRect.xywh(x0 + wPlusMrg * 2, hPlusMrg + y0, w, h), // g
                TestRect.xywh(x0 + wPlusMrg * 3, hPlusMrg + y0, w, h), // h
                TestRect.xywh(x0 + wPlusMrg * 4, hPlusMrg + y0, w, h), // i
                TestRect.xywh(x0 + 0f, hPlusMrg * 2 + y0, w, h), // j
                TestRect.xywh(x0 + wPlusMrg, hPlusMrg * 2 + y0, w, h), // k
                TestRect.xywh(x0 + wPlusMrg * 2, hPlusMrg * 2 + y0, w, h) // l
            )
        )
    }

    @Test
    fun draw_wrap_LeadingSpace_break_revealed_wordsAfterWrap() {
        testDraw(
            width = 86, // 12*2+2 + 12*2 + 12*2+2 + 5*2
            words = listOf(
                Word.fromValue("ab", 0).resolvedVersion,
                Word.fromValue(", ", 1, true),
                Word.fromValue("efghijkl", 2).resolvedVersion,
                Word.fromValue(" ", 3, true),
                Word.fromValue("cd", 4).resolvedVersion
            ),
            texts = listOf(
                TestTextShape("a", x0 + 0f + textDx, y0 + textDy),
                TestTextShape("b", x0 + wPlusMrg + textDx, y0 + textDy),
                TestTextShape(",", x0 + wPlusMrg + w + textDx, y0 + textDy),
                TestTextShape(" ", x0 + wPlusMrg + w + w + textDx, y0 + textDy),
                TestTextShape("e", x0 + 0f + textDx, hPlusMrg + y0 + textDy),
                TestTextShape("f", x0 + wPlusMrg + textDx, hPlusMrg + y0 + textDy),
                TestTextShape("g", x0 + 2 * wPlusMrg + textDx, hPlusMrg + y0 + textDy),
                TestTextShape("h", x0 + 3 * wPlusMrg + textDx, hPlusMrg + y0 + textDy),
                TestTextShape("i", x0 + 4 * wPlusMrg + textDx, hPlusMrg + y0 + textDy),
                TestTextShape("j", x0 + 0f + textDx, 2 * hPlusMrg + y0 + textDy),
                TestTextShape("k", x0 + wPlusMrg + textDx, 2 * hPlusMrg + y0 + textDy),
                TestTextShape("l", x0 + 2 * wPlusMrg + textDx, 2 * hPlusMrg + y0 + textDy),
                TestTextShape(" ", x0 + 2 * wPlusMrg + w + textDx, 2 * hPlusMrg + y0 + textDy),
                TestTextShape("c", x0 + 0f + textDx, 3 * hPlusMrg + y0 + textDy),
                TestTextShape("d", x0 + wPlusMrg + textDx, 3 * hPlusMrg + y0 + textDy)
            ),
            rects = emptyList()
        )
    }

    @Test
    fun draw_wrapAtEndOfRow() {
        testDraw(
            width = 38,
            words = listOf(
                Word.fromValue("Abcd", 0).resolvedVersion,
                Word.fromValue("-", 1, true),
                Word.fromValue("ef", 2).resolvedVersion
            ),
            texts = listOf(
                TestTextShape("A", x0 + 0f + textDx, y0 + textDy),
                TestTextShape("b", x0 + wPlusMrg + textDx, y0 + textDy),
                TestTextShape("c", x0 + 0f + textDx, hPlusMrg + y0 + textDy),
                TestTextShape("d", x0 + wPlusMrg + textDx, hPlusMrg + y0 + textDy),
                TestTextShape("-", x0 + 0f + textDx, 2 * hPlusMrg + y0 + textDy),
                TestTextShape("e", x0 + 0f + textDx, 3 * hPlusMrg + y0 + textDy),
                TestTextShape("f", x0 + wPlusMrg + textDx, 3 * hPlusMrg + y0 + textDy)
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
