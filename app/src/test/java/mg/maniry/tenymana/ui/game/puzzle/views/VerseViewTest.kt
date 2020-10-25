package mg.maniry.tenymana.ui.game.puzzle.views

import com.google.common.truth.Truth.assertThat
import mg.maniry.tenymana.gameLogic.models.Word
import org.junit.Test

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
}
