package mg.maniry.tenymana.ui.views.hiddenWords

import android.graphics.Canvas
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.clearInvocations
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.mock
import mg.maniry.tenymana.ui.views.hiddenWords.HiddenWordsInputViewControl.Companion.CELL_MARGIN
import mg.maniry.tenymana.ui.views.hiddenWords.HiddenWordsInputViewControl.Companion.CELL_SIZE
import mg.maniry.tenymana.ui.views.hiddenWords.HiddenWordsInputViewControl.Companion.PADDING
import mg.maniry.tenymana.utils.*
import org.junit.Test

class HiddenWordsInputViewControlTest {
    private val sizePlusM = CELL_SIZE + CELL_MARGIN

    @Test
    fun layoutAndClickHandler() {
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
        val control = HiddenWordsInputViewControl()
        fun HiddenWordsInputViewControl.reDraw() {
            rects.removeAll { true }
            texts.removeAll { true }
            draw(canvas)
        }

        val width = (PADDING * 2 + CELL_SIZE * 3 + CELL_MARGIN * 2).toInt()
        control.word = chars('a', 'b', 'c')
        control.onMeasure(width)
        assertThat(control.height).isEqualTo((PADDING * 2 + CELL_SIZE).toInt())
        // Anim & draw
        val finalRects = listOf(
            TestRect.xywh(PADDING, PADDING, CELL_SIZE, CELL_SIZE),
            TestRect.xywh(PADDING + sizePlusM, PADDING, CELL_SIZE, CELL_SIZE),
            TestRect.xywh(PADDING + 2 * sizePlusM, PADDING, CELL_SIZE, CELL_SIZE)
        )
        control.startAnim(100L)
        control.reDraw()
        assertThat(rects).isEqualTo(finalRects.map {
            TestRect.xywh(
                0f,
                it.top,
                CELL_SIZE,
                CELL_SIZE
            )
        })
        assertThat(texts.size).isEqualTo(3)
        // 1.0
        var invalidate = control.onTick(100L + BaseHiddenWordsViewControl.ANIM_DURATION / 10)
        assertThat(invalidate).isTrue()
        control.reDraw()
        assertThat(rects).isEqualTo(
            finalRects.map { TestRect.xywh(it.left / 10, it.top, CELL_SIZE, CELL_SIZE) }
        )
        // After anim
        invalidate = control.onTick(100L + BaseHiddenWordsViewControl.ANIM_DURATION)
        assertThat(invalidate).isFalse()
        control.reDraw()
        assertThat(rects).isEqualTo(finalRects)
        assertThat(texts.size).isEqualTo(3)
        // two rows
        control.word = chars('a', 'b', 'c', 'd')
        assertThat(control.height).isEqualTo((PADDING * 2 + CELL_SIZE * 2 + CELL_MARGIN).toInt())
        // click
        val selectHandler: (Int) -> Unit = mock()
        control.onSelect(selectHandler)
        // outside drawing area
        control.onClick(0, 0)
        verifyNever(selectHandler)(any())
        control.onClick(width - 1, control.height - 1)
        verifyNever(selectHandler)(any())
        control.onClick(width - 1, PADDING.toInt() + 1)
        verifyNever(selectHandler)(any())
        control.onClick((PADDING + CELL_SIZE + 1).toInt(), PADDING.toInt() + 1)
        verifyNever(selectHandler)(any())
        // cell 0
        control.onClick(PADDING.toInt() + 1, PADDING.toInt() + 1)
        verifyOnce(selectHandler)(0)
        // cell 1
        clearInvocations(selectHandler)
        control.onClick((PADDING + sizePlusM + 1).toInt(), PADDING.toInt() + 1)
        verifyOnce(selectHandler)(1)
        // cell 3
        clearInvocations(selectHandler)
        control.onClick((width / 2), (PADDING + sizePlusM + 1).toInt())
        verifyOnce(selectHandler)(3)
        // Update word
        control.word = chars('a', null, null, null)
        texts.removeAll { true }
        control.draw(canvas)
        assertThat(texts.size).isEqualTo(1)
    }
}
