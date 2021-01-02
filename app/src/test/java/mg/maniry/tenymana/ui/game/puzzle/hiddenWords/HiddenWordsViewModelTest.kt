package mg.maniry.tenymana.ui.game.puzzle.hiddenWords

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.*
import mg.maniry.tenymana.gameLogic.hiddenWords.HiddenWordsGroup
import mg.maniry.tenymana.gameLogic.hiddenWords.HiddenWordsPuzzle
import mg.maniry.tenymana.gameLogic.models.BibleVerse
import mg.maniry.tenymana.gameLogic.models.Puzzle
import mg.maniry.tenymana.ui.game.puzzle.PuzzleViewModel
import mg.maniry.tenymana.utils.chars
import mg.maniry.tenymana.utils.verifyOnce
import org.junit.Rule
import org.junit.Test

class HiddenWordsViewModelTest {
    @get:Rule
    val liveDataRule = InstantTaskExecutorRule()

    @Test
    fun test() {
        val verse = BibleVerse.fromText("", 1, 1, "Abc ddef gh jkl mn")
        var groups = listOf(
            HiddenWordsGroup(chars('a', 'b', 'c'), verse.words[2], false),
            HiddenWordsGroup(chars('g', 'h', 'm', 'n'), verse.words[6], false)
        )
        var proposeResult = false
        val puzzle: HiddenWordsPuzzle = mock {
            on { this.verse } doReturn verse
            on { this.groups } doAnswer { groups }
            on { this.propose(any(), any()) } doAnswer { proposeResult }
        }
        val puzzleLD: LiveData<Puzzle?> = MutableLiveData(puzzle)
        val puzzleVM: PuzzleViewModel = mock {
            on { this.puzzle } doReturn puzzleLD
        }
        val viewModel = HiddenWordsViewModel(puzzleVM)
        // Initial values
        assertThat(viewModel.words.value).isEqualTo(verse.words)
        assertThat(viewModel.groups.value).isEqualTo(groups)
        assertThat(viewModel.characters.value).isEqualTo(
            listOf(
                chars('a', 'b', 'c'),
                chars('g', 'h', 'm', 'n')
            )
        )
        // select
        viewModel.onSelect(0, 1)
        assertThat(viewModel.characters.value).isEqualTo(
            listOf(
                chars('a', null, 'c'),
                chars('g', 'h', 'm', 'n')
            )
        )
        // cancel
        viewModel.cancel()
        assertThat(viewModel.characters.value).isEqualTo(
            listOf(chars('a', 'b', 'c'), chars('g', 'h', 'm', 'n'))
        )
        // select
        viewModel.onSelect(1, 2)
        viewModel.onSelect(1, 3)
        viewModel.onSelect(1, 8)
        assertThat(viewModel.characters.value).isEqualTo(
            listOf(chars('a', 'b', 'c'), chars('g', 'h', null, null))
        )
        // Propose -> false
        viewModel.propose(1)
        verifyOnce(puzzle).propose(1, listOf(2, 3))
        assertThat(viewModel.groups.value).isEqualTo(groups)
        assertThat(viewModel.characters.value).isEqualTo(
            listOf(chars('a', 'b', 'c'), chars('g', 'h', 'm', 'n'))
        )
        // Propose -> true
        groups = listOf(
            groups[0],
            groups[1].copy(chars = chars(null, null, 'm', 'n'))
        )
        proposeResult = true
        clearInvocations(puzzle)
        viewModel.onSelect(1, 0)
        viewModel.onSelect(1, 1)
        assertThat(viewModel.characters.value).isEqualTo(
            listOf(chars('a', 'b', 'c'), chars(null, null, 'm', 'n'))
        )
        viewModel.propose(1)
        verifyOnce(puzzle).propose(1, listOf(0, 1))
        assertThat(viewModel.groups.value).isEqualTo(groups)
        // Select & cancel
        viewModel.onSelect(1, 2)
        viewModel.cancel()
        assertThat(viewModel.characters.value).isEqualTo(
            listOf(chars('a', 'b', 'c'), chars(null, null, 'm', 'n'))
        )
        // Resolve group
        clearInvocations(puzzle)
        groups = listOf(
            groups[0],
            groups[1].copy(resolved = true, chars = chars(null, null, null, null))
        )
        viewModel.onSelect(1, 2)
        viewModel.onSelect(1, 3)
        viewModel.propose(1)
        verifyOnce(puzzle).propose(1, listOf(2, 3))
        assertThat(viewModel.groups.value).isEqualTo(groups)
        assertThat(viewModel.characters.value).isEqualTo(
            listOf(chars('a', 'b', 'c'), chars(null, null, null, null))
        )
        // Complete game
        groups = listOf(
            groups[0].copy(resolved = true, chars = chars(null, null, null)),
            groups[1]
        )
        viewModel.onSelect(0, 0)
        viewModel.onSelect(0, 1)
        viewModel.onSelect(0, 2)
        viewModel.propose(0)
        verifyOnce(puzzle).propose(0, listOf(0, 1, 2))
        verifyOnce(puzzleVM).onComplete()
    }
}
