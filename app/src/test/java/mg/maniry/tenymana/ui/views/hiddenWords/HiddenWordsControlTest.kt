package mg.maniry.tenymana.ui.views.hiddenWords

import com.google.common.truth.Truth.assertThat
import mg.maniry.tenymana.ui.views.hiddenWords.HiddenWordsControl.Companion.HEIGHT
import mg.maniry.tenymana.ui.views.hiddenWords.HiddenWordsControl.Companion.MARGIN_H
import mg.maniry.tenymana.ui.views.hiddenWords.HiddenWordsControl.Companion.MARGIN_V
import mg.maniry.tenymana.ui.views.hiddenWords.HiddenWordsControl.Companion.PADDING
import mg.maniry.tenymana.ui.views.hiddenWords.HiddenWordsControl.Companion.WIDTH
import mg.maniry.tenymana.utils.chars
import org.junit.Test

class HiddenWordsControlTest {
    @Test
    fun layout() {
        val control = HiddenWordsControl()
        control.onMeasure(WIDTH * 3 + MARGIN_H * 2 + PADDING * 2)
        assertThat(control.height).isEqualTo(PADDING * 2)
        // one row
        control.word = chars('a', 'b', 'c')
        assertThat(control.height).isEqualTo(PADDING * 2 + HEIGHT)
        // two rows
        control.word = chars('a', 'b', 'c', 'd')
        assertThat(control.height).isEqualTo(PADDING * 2 + HEIGHT * 2 + MARGIN_V)
        // one row
        control.onMeasure(WIDTH * 100)
        assertThat(control.height).isEqualTo(PADDING * 2 + HEIGHT)
    }
}
