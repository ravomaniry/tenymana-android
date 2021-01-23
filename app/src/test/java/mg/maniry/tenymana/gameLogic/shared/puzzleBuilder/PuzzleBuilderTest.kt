package mg.maniry.tenymana.gameLogic.shared.puzzleBuilder

import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.doReturnConsecutively
import com.nhaarman.mockitokotlin2.mock
import mg.maniry.tenymana.gameLogic.anagram.AnagramPuzzle
import mg.maniry.tenymana.gameLogic.hiddenWords.HiddenWordsPuzzle
import mg.maniry.tenymana.gameLogic.linkClear.LinkClearPuzzle
import mg.maniry.tenymana.gameLogic.models.BibleVerse
import mg.maniry.tenymana.utils.Random
import org.junit.Test

@Suppress("unchecked_cast")
class PuzzleBuilderTest {
    @Test
    fun test() {
        val randoms = listOf(
            0.8, 0.5, 0.5, // link clear
            0.5, 0.8, 0.7, // hidden words
            0.8, 0.9, 0.99 // anagram
        )
        val random: Random = mock {
            on { this.double() } doReturnConsecutively randoms
        }
        val verse = BibleVerse.fromText("", 1, 1, "abc")
        val builder = PuzzleBuilderImpl(random)
        // Puzzles
        assertThat(builder.random(verse) is LinkClearPuzzle).isTrue()
        assertThat(builder.random(verse) is HiddenWordsPuzzle).isTrue()
        assertThat(builder.random(verse) is AnagramPuzzle).isTrue()
    }
}
