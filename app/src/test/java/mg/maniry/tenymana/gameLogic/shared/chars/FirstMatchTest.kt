package mg.maniry.tenymana.gameLogic.shared.chars

import com.google.common.truth.Truth.assertThat
import mg.maniry.tenymana.gameLogic.models.Character
import mg.maniry.tenymana.gameLogic.models.Word
import mg.maniry.tenymana.utils.chars
import org.junit.Test

class FirstMatchTest {
    val words = listOf(
        Word.fromValue("Abc", 0),
        Word.fromValue("cd", 0)
    )

    @Test
    fun noNull_firstValue() {
        testMatch(chars('a', 'b', 'c', 'a'), words, 0)
    }

    @Test
    fun withNull_secondValue() {
        testMatch(chars('c', null, 'd', null), words, 1)
    }

    @Test
    fun noNull_noMatch() {
        testMatch(chars('a', 'b'), words, -1)
    }

    @Test
    fun withNull_noMatch() {
        testMatch(chars('a', 'b'), words, -1)
    }

    @Test
    fun withResolved() {
        testMatch(
            chars('c', 'd'),
            listOf(words[0], words[1].resolvedVersion),
            -1
        )
    }

    private fun testMatch(chars: List<Character?>, words: List<Word>, match: Int) {
        assertThat(chars.firstMatch(words)).isEqualTo(match)
    }
}
