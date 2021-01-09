package mg.maniry.tenymana.gameLogic.shared.puzzleBuilder

import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doAnswer
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
        var randIndex = 0
        val random: Random = mock {
            on { this.from(any<List<PuzzleBuilderImpl.GameTypes>>()) } doAnswer {
                (it.arguments[0] as List<PuzzleBuilderImpl.GameTypes>)[randIndex]
            }
        }
        val verse = BibleVerse.fromText("", 1, 1, "abc")
        val builder = PuzzleBuilderImpl(random)
        // Link clear
        randIndex = 0
        assertThat(builder.random(verse) is LinkClearPuzzle).isTrue()
        // Hidden words
        randIndex = 1
        assertThat(builder.random(verse) is HiddenWordsPuzzle).isTrue()
        // Anagram
        randIndex = 2
        assertThat(builder.random(verse) is AnagramPuzzle).isTrue()
    }
}
