package mg.maniry.tenymana.ui.views.hiddenWords

import android.graphics.Canvas
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.mock
import mg.maniry.tenymana.gameLogic.models.Character
import mg.maniry.tenymana.ui.views.hiddenWords.HiddenWordViewControl.Companion.BG_OFFSET
import mg.maniry.tenymana.ui.views.hiddenWords.HiddenWordViewControl.Companion.HEIGHT
import mg.maniry.tenymana.ui.views.hiddenWords.HiddenWordViewControl.Companion.MARGIN_H
import mg.maniry.tenymana.ui.views.hiddenWords.HiddenWordViewControl.Companion.MARGIN_V
import mg.maniry.tenymana.ui.views.hiddenWords.HiddenWordViewControl.Companion.PADDING
import mg.maniry.tenymana.ui.views.hiddenWords.HiddenWordViewControl.Companion.WIDTH
import mg.maniry.tenymana.utils.TestRect
import mg.maniry.tenymana.utils.TestTextShape
import mg.maniry.tenymana.utils.chars
import org.junit.Test

@Suppress("unchecked_cast")
class HiddenWordsControlTest {
    private val wPlusM = WIDTH + MARGIN_H
    private val bgPadding = PADDING + BG_OFFSET

    @Test
    fun layout_and_draw() {
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
        val control = HiddenWordViewControl()
        control.onMeasure((WIDTH * 3 + MARGIN_H * 2 + PADDING * 2 + 6).toInt())
        assertThat(control.height).isEqualTo((PADDING * 2).toInt())
        control.draw(canvas)
        assertThat(rects).isEmpty()
        assertThat(texts).isEmpty()
        // one row
        control.word = chars('a', 'b', 'c') as List<Character>
        assertThat(control.height).isEqualTo((PADDING * 2 + HEIGHT).toInt())
        control.draw(canvas)
        assertThat(texts).isEmpty()
        assertThat(rects).isEqualTo(
            listOf(
                TestRect.xywh(bgPadding + 3, bgPadding, WIDTH, HEIGHT),
                TestRect.xywh(PADDING + 3, PADDING, WIDTH, HEIGHT),
                TestRect.xywh(bgPadding + 3 + wPlusM, bgPadding, WIDTH, HEIGHT),
                TestRect.xywh(PADDING + 3 + wPlusM, PADDING, WIDTH, HEIGHT),
                TestRect.xywh(bgPadding + 3 + 2 * wPlusM, bgPadding, WIDTH, HEIGHT),
                TestRect.xywh(PADDING + 3 + 2 * wPlusM, PADDING, WIDTH, HEIGHT)
            )
        )
        // two rows
        control.word = chars('a', 'b', 'c', 'd') as List<Character>
        assertThat(control.height).isEqualTo((PADDING * 2 + HEIGHT * 2 + MARGIN_V).toInt())
        // one row
        rects.removeAll { true }
        control.resolved = true
        control.onMeasure((WIDTH * 100).toInt())
        assertThat(control.height).isEqualTo((PADDING * 2 + HEIGHT).toInt())
        control.draw(canvas)
        assertThat(texts).isNotEmpty()
    }
}
