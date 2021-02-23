package mg.maniry.tenymana.gameLogic.anagram

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import mg.maniry.tenymana.gameLogic.models.BibleVerse
import mg.maniry.tenymana.gameLogic.models.Character
import mg.maniry.tenymana.utils.chars
import org.junit.Rule
import org.junit.Test

@Suppress("unchecked_cast")
class PuzzleTest {
    @get:Rule
    val ldRule = InstantTaskExecutorRule()

    @Test
    fun puzzle() {
        val verse = BibleVerse.fromText("", 1, 1, "Abc i de ab abc")
        val initialChars = listOf(
            chars('a', 'c', 'b') as List<Character>,
            chars('e', 'd') as List<Character>,
            chars('a', 'b') as List<Character>
        )
        val puzzle = AnagramPuzzleImpl(initialChars, verse)
        // initial values
        assertThat(puzzle.chars).isEqualTo(chars('a', 'c', 'b'))
        assertThat(puzzle.verse).isEqualTo(verse)
        // Propose -> false
        assertThat(puzzle.propose(listOf(0, 2))).isFalse()
        assertThat(puzzle.propose(listOf(2, 1, 0))).isFalse()
        assertThat(puzzle.chars).isEqualTo(chars('a', 'c', 'b'))
        assertThat(puzzle.verse).isEqualTo(verse)
        assertThat(puzzle.completed).isFalse()
        // propose -> true
        val words = verse.words.toMutableList()
        words[0] = words[0].resolvedVersion
        words[2] = words[2].resolvedVersion
        words[8] = words[8].resolvedVersion
        var didUpdate = puzzle.propose(listOf(0, 2, 1))
        assertThat(didUpdate).isTrue()
        assertThat(puzzle.chars).isEqualTo(chars('e', 'd'))
        assertThat(puzzle.verse.words).isEqualTo(words)
        assertThat(puzzle.score.value).isEqualTo(7)
        assertThat(puzzle.completed).isFalse()
        // resolve -> true
        didUpdate = puzzle.propose(listOf(1, 0))
        assertThat(didUpdate).isTrue()
        assertThat(puzzle.chars).isEqualTo(chars('a', 'b'))
        assertThat(puzzle.verse.words[4].resolved).isTrue()
        assertThat(puzzle.score.value).isEqualTo(9)
        assertThat(puzzle.completed).isFalse()
        // resolve -> true -> complete
        didUpdate = puzzle.propose(listOf(0, 1))
        assertThat(didUpdate).isTrue()
        assertThat(puzzle.completed).isTrue()
        assertThat(puzzle.verse.words[6].resolved).isTrue()
        assertThat(puzzle.score.value).isEqualTo((9 + 2) * 2)
    }
}
