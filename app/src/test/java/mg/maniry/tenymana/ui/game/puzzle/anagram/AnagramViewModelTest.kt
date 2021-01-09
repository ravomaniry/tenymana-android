package mg.maniry.tenymana.ui.game.puzzle.anagram

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.*
import mg.maniry.tenymana.gameLogic.anagram.AnagramPuzzle
import mg.maniry.tenymana.gameLogic.models.BibleVerse
import mg.maniry.tenymana.gameLogic.models.Character
import mg.maniry.tenymana.gameLogic.models.Puzzle
import mg.maniry.tenymana.ui.game.puzzle.PuzzleViewModel
import mg.maniry.tenymana.utils.chars
import mg.maniry.tenymana.utils.verifyNever
import mg.maniry.tenymana.utils.verifyOnce
import org.junit.Rule
import org.junit.Test

class AnagramViewModelTest {
    @get:Rule
    val ldRule = InstantTaskExecutorRule()

    @Test
    @Suppress("unchecked_cast")
    fun test() {
        var verse = BibleVerse.fromText("", 1, 1, "Abc de")
        var charsVaues: List<Character> = chars('d', 'e') as List<Character>
        var completed = false
        var proposeResult = false
        val puzzle: AnagramPuzzle = mock {
            on { this.chars } doAnswer { charsVaues }
            on { this.completed } doAnswer { completed }
            on { this.verse } doAnswer { verse }
            on { this.propose(any()) } doAnswer { proposeResult }
        }
        val puzzleViewModel: PuzzleViewModel = mock {
            on { this.puzzle } doReturn MutableLiveData(puzzle) as LiveData<Puzzle?>
        }
        val viewModel = AnagramViewModel(puzzleViewModel)
        // initial values
        assertThat(viewModel.words.value).isEqualTo(verse.words)
        assertThat(viewModel.characters.value).isEqualTo(charsVaues)
        assertThat(viewModel.animate.value).isTrue()
        viewModel.onAnimationDone()
        // 1- select chars
        viewModel.onCharSelect(0)
        assertThat(viewModel.characters.value).isEqualTo(chars(null, 'e'))
        assertThat(viewModel.proposition.value).isEqualTo("D")
        // 2- cancel
        viewModel.cancel()
        assertThat(viewModel.characters.value).isEqualTo(charsVaues)
        assertThat(viewModel.proposition.value).isEqualTo("")
        assertThat(viewModel.animate.value).isFalse()
        // 3- select -> propose -> false
        viewModel.onCharSelect(1)
        viewModel.onCharSelect(1)
        viewModel.onCharSelect(0)
        assertThat(viewModel.characters.value).isEqualTo(chars(null, null))
        assertThat(viewModel.proposition.value).isEqualTo("ED")
        viewModel.propose()
        verifyOnce(puzzle).propose(listOf(1, 0))
        assertThat(viewModel.characters.value).isEqualTo(charsVaues)
        assertThat(viewModel.proposition.value).isEqualTo("")
        assertThat(viewModel.animate.value).isFalse()
        // 4- select -> propose -> true
        val nextWords = verse.words.toMutableList()
        nextWords[2] = nextWords[2].resolvedVersion
        verse = verse.copy(words = nextWords)
        proposeResult = true
        charsVaues = chars('a', 'b', 'c') as List<Character>
        clearInvocations(puzzle)
        viewModel.onCharSelect(0)
        viewModel.onCharSelect(1)
        viewModel.propose()
        assertThat(viewModel.characters.value).isEqualTo(charsVaues)
        assertThat(viewModel.proposition.value).isEqualTo("")
        assertThat(viewModel.words.value).isEqualTo(nextWords)
        assertThat(viewModel.animate.value).isTrue()
        verifyNever(puzzleViewModel).onComplete()
        // 5- select -> propose -> true -> complete
        clearInvocations(puzzle)
        completed = true
        viewModel.onCharSelect(0)
        viewModel.onCharSelect(1)
        viewModel.onCharSelect(2)
        viewModel.propose()
        verifyOnce(puzzle).propose(listOf(0, 1, 2))
        verifyOnce(puzzleViewModel).onComplete()
    }
}
