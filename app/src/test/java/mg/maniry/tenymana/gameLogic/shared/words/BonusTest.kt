package mg.maniry.tenymana.gameLogic.shared.words

import com.google.common.truth.Truth.assertThat
import mg.maniry.tenymana.gameLogic.models.BibleVerse
import org.junit.Test

class BonusTest {
    @Test
    fun bonus() {
        val words = BibleVerse.fromText("", 1, 1, "Abc de fg hi").words.toMutableList()
        assertThat(words.bonusRatio).isEqualTo(0)
        words[0] = words[0].resolvedVersion
        assertThat(words.bonusRatio).isEqualTo(0.25)
        words[2] = words[2].resolvedVersion
        assertThat(words.bonusRatio).isEqualTo(0.5)
        words[4] = words[4].resolvedVersion
        words[6] = words[6].resolvedVersion
        assertThat(words.bonusRatio).isEqualTo(1)
    }
}
