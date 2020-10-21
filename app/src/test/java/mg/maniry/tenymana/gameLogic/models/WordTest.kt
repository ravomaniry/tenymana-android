package mg.maniry.tenymana.gameLogic.models

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class WordTest {
    @Test
    fun firstUnrevealedIndex() {
        val word = Word.fromValue("ABCD", 0, false);
        assertThat(word.firstUnrevealedIndex).isEqualTo(0)
        val word1 = word.copyWithChar(0, word.chars[0].copy(resolved = true))
        assertThat(word1.firstUnrevealedIndex).isEqualTo(1)
    }
}
