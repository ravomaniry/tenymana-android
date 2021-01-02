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
        val width = (PADDING * 2 + CELL_SIZE * 3 + CELL_MARGIN * 2).toInt()
        control.word = chars('a', 'b', 'c')
        control.onMeasure(width)
        assertThat(control.height).isEqualTo((PADDING * 2 + CELL_SIZE).toInt())
        control.draw(canvas)
        assertThat(rects).isEqualTo(
            listOf(
                TestRect.xywh(PADDING, PADDING, CELL_SIZE, CELL_SIZE),
                TestRect.xywh(PADDING + sizePlusM, PADDING, CELL_SIZE, CELL_SIZE),
                TestRect.xywh(PADDING + 2 * sizePlusM, PADDING, CELL_SIZE, CELL_SIZE)
            )
        )
        assertThat(texts.size).isEqualTo(3)
        // two rows
        control.word = chars('a', 'b', 'c', 'd')
        assertThat(control.height).isEqualTo((PADDING * 2 + CELL_SIZE * 2 + CELL_MARGIN).toInt())
        // click
        val groupIndex = 1
        val selectHandler: (Int, Int) -> Unit = mock()
        control.groupIndex = groupIndex
        control.onSelect(selectHandler)
        // outside drawing area
        control.onClick(0, 0)
        verifyNever(selectHandler)(any(), any())
        control.onClick(width - 1, control.height - 1)
        verifyNever(selectHandler)(any(), any())
        control.onClick(width - 1, PADDING.toInt() + 1)
        verifyNever(selectHandler)(any(), any())
        control.onClick((PADDING + CELL_SIZE + 1).toInt(), PADDING.toInt() + 1)
        verifyNever(selectHandler)(any(), any())
        // cell 0
        control.onClick(PADDING.toInt() + 1, PADDING.toInt() + 1)
        verifyOnce(selectHandler)(groupIndex, 0)
        // cell 1
        clearInvocations(selectHandler)
        control.onClick((PADDING + sizePlusM + 1).toInt(), PADDING.toInt() + 1)
        verifyOnce(selectHandler)(groupIndex, 1)
        // cell 3
        clearInvocations(selectHandler)
        control.onClick((width / 2), (PADDING + sizePlusM + 1).toInt())
        verifyOnce(selectHandler)(groupIndex, 3)
    }
}
