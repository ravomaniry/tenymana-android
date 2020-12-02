package mg.maniry.tenymana.ui.views.verse

import android.graphics.Canvas
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.mock
import mg.maniry.tenymana.gameLogic.models.Word
import mg.maniry.tenymana.ui.views.settings.DrawingSettings
import mg.maniry.tenymana.ui.views.verse.BaseVerseViewControl.Companion.H
import mg.maniry.tenymana.ui.views.verse.BaseVerseViewControl.Companion.LINE_H
import mg.maniry.tenymana.ui.views.verse.BaseVerseViewControl.Companion.PADDING
import mg.maniry.tenymana.ui.views.verse.BaseVerseViewControl.Companion.SPACING_H
import mg.maniry.tenymana.ui.views.verse.BaseVerseViewControl.Companion.SPACING_V
import mg.maniry.tenymana.ui.views.verse.BaseVerseViewControl.Companion.W
import mg.maniry.tenymana.utils.TestRect
import org.junit.Test

class AnimVerseViewControlTest {
    private val padding = PADDING.toFloat()
    private val h = H.toFloat()
    private val w = W.toFloat()
    private val lineH = LINE_H.toFloat()
    private val wPlusSp = w + SPACING_H

    @Test
    fun oneWord_oneRow() {
        // mocks
        val rects = mutableListOf<TestRect>()
        val canvas: Canvas = mock {
            on { drawRect(any(), any(), any(), any(), any()) } doAnswer {
                rects.add(TestRect.fromMock(it.arguments)); Unit
            }
        }
        // render
        val control = AnimVerseViewControl()
        // reRender
        val reRender: (Long) -> Boolean = {
            rects.removeAll { true }
            val invalidate = control.onTick(it)
            control.draw(canvas)
            invalidate
        }
        control.settings = DrawingSettings()
        val words = mutableListOf(
            Word.fromValue("Abc", 0), // 12 * 3 + 2 * 2 = 40 | 0
            Word.fromValue(", ", 1, true), // 12 * 2 = 24 | 0
            Word.fromValue("de", 2), // 12 * 2 + 2 = 26 | 0
            Word.fromValue(" ", 3, true), // 12 | 1
            Word.fromValue("fgh", 4), // 12 * 3 + 2 * 2 = 40 | 1
            Word.fromValue(" ", 5, true),
            Word.fromValue("abcdefghi", 6) // 14*7 + 14*2
        )
        control.onWordsChange(words)
        control.onMeasure(100)
        assertThat(control.settings?.verseViewHeight).isEqualTo(4 * H + SPACING_V * 3 + 2 * PADDING)
        // Nothing to animate
        control.draw(canvas)
        assertThat(rects).isEmpty()
        // anim first word
        words[0] = words[0].resolvedVersion
        control.startAnim(1000)
        // - draw full rects
        control.draw(canvas)
        assertThat(rects).isEqualTo(
            listOf(
                TestRect.xywh(padding, padding, w, h),
                TestRect.xywh(padding + wPlusSp, padding, w, h),
                TestRect.xywh(padding + 2 * wPlusSp, padding, w, h)
            )
        )
        // Move to 10
        val inv = reRender(1050)
        assertThat(inv).isTrue()
        assertThat(rects).isEqualTo(
            listOf(
                TestRect.xywh(padding + 10, padding, w, h),
                TestRect.xywh(padding + wPlusSp, padding, w, h),
                TestRect.xywh(padding + 2 * wPlusSp, padding, w, h)
            )
        )
        // Move to 20: do not draw cell[0]
        val inv1 = reRender(1100)
        assertThat(inv1).isTrue()
        assertThat(rects).isEqualTo(
            listOf(
                TestRect.xywh(padding + 20, padding, w, h),
                TestRect.xywh(padding + 2 * wPlusSp, padding, w, h)
            )
        )
        // Move to 30
        val inv2 = reRender(1150)
        assertThat(inv2).isTrue()
        assertThat(rects).isEqualTo(listOf(TestRect.xywh(padding + 30, padding, w, h)))
        // Complete
        val inv3 = reRender(1200)
        assertThat(inv3).isFalse()
        assertThat(rects).isEmpty()
        // Two words
        words[2] = words[2].resolvedVersion
        words[4] = words[4].resolvedVersion
        control.startAnim(2000)
        // -> +5 at row 0
        // -> +10 at row 1
        reRender(2050)
        assertThat(rects).isEqualTo(
            listOf(
                TestRect.xywh(padding + 64 + 6.5f, padding, w, h),
                TestRect.xywh(padding + 64 + wPlusSp, padding, w, h),
                TestRect.xywh(padding + 10, padding + lineH, w, h),
                TestRect.xywh(padding + wPlusSp, padding + lineH, w, h),
                TestRect.xywh(padding + 2 * wPlusSp, padding + lineH, w, h)
            )
        )
        // Broken word
        words[6] = words[6].resolvedVersion
        control.startAnim(3000)
        reRender(3050)
        assertThat(rects).isEqualTo(
            listOf(
                TestRect.xywh(padding + 20.5f, padding + 2 * lineH, w, h),
                TestRect.xywh(padding + 2 * wPlusSp, padding + 2 * lineH, w, h),
                TestRect.xywh(padding + 3 * wPlusSp, padding + 2 * lineH, w, h),
                TestRect.xywh(padding + 4 * wPlusSp, padding + 2 * lineH, w, h),
                TestRect.xywh(padding + 5 * wPlusSp, padding + 2 * lineH, w, h),
                TestRect.xywh(padding + 10, padding + 3 * lineH, w, h),
                TestRect.xywh(padding + wPlusSp, padding + 3 * lineH, w, h),
                TestRect.xywh(padding + 2 * wPlusSp, padding + 3 * lineH, w, h)
            )
        )
    }
}
