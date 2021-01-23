package mg.maniry.tenymana.ui.game.puzzle.hiddenWords

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import mg.maniry.tenymana.gameLogic.hiddenWords.HiddenWordsGroup
import mg.maniry.tenymana.gameLogic.hiddenWords.HiddenWordsPuzzle
import mg.maniry.tenymana.gameLogic.hiddenWords.HiddenWordsPuzzleImpl
import mg.maniry.tenymana.gameLogic.models.BibleVerse
import mg.maniry.tenymana.gameLogic.models.Puzzle
import mg.maniry.tenymana.gameLogic.shared.words.unresolvedChar
import mg.maniry.tenymana.ui.game.puzzle.PuzzleViewModel
import mg.maniry.tenymana.utils.*
import org.junit.Rule
import org.junit.Test

@Suppress("unchecked_cast")
@ExperimentalCoroutinesApi
class HiddenWordsViewModelTest {
    @get:Rule
    val liveDataRule = InstantTaskExecutorRule()

    @Test
    fun test() {
        var verse = BibleVerse.fromText("", 1, 1, "Abc ddef gh jkl mn")
        var groups = listOf(
            HiddenWordsGroup(chars('a', 'b', 'c'), verse.words[2], false),
            HiddenWordsGroup(chars('g', 'h', 'm', 'n'), verse.words[6], false)
        )
        var proposeResult = false
        var completed = false
        val puzzle: HiddenWordsPuzzle = mock {
            on { this.verse } doAnswer { verse }
            on { this.groups } doAnswer { groups.toList() }
            on { this.completed } doAnswer { completed }
            on { this.propose(any(), any()) } doAnswer { proposeResult }
        }
        val puzzleLD: LiveData<Puzzle?> = MutableLiveData(puzzle)
        val puzzleVM: PuzzleViewModel = mock {
            on { this.puzzle } doReturn puzzleLD
        }
        val viewModel = HiddenWordsViewModel(puzzleVM, TestDispatchers)
        // Initial values
        assertThat(viewModel.proposition.value).isEqualTo("")
        assertThat(viewModel.words.value).isEqualTo(verse.words)
        assertThat(viewModel.activeGroup.value).isEqualTo(groups[0])
        assertThat(viewModel.characters.value).isEqualTo(chars('a', 'b', 'c'))
        // select
        viewModel.onCharSelect(1)
        assertThat(viewModel.proposition.value).isEqualTo("B")
        assertThat(viewModel.characters.value).isEqualTo(chars('a', null, 'c'))
        // change active group
        viewModel.activateNexGroup()
        assertThat(viewModel.proposition.value).isEqualTo("")
        assertThat(viewModel.characters.value).isEqualTo(groups[1].chars)
        // no submit
        viewModel.propose()
        verifyNever(puzzle).propose(any(), any())
        // select & cancel
        viewModel.onCharSelect(0)
        assertThat(viewModel.proposition.value).isEqualTo("G")
        assertThat(viewModel.characters.value).isEqualTo(chars(null, 'h', 'm', 'n'))
        viewModel.cancel()
        assertThat(viewModel.proposition.value).isEqualTo("")
        assertThat(viewModel.characters.value).isEqualTo(groups[1].chars)
        // select
        viewModel.onCharSelect(2)
        viewModel.onCharSelect(3)
        viewModel.onCharSelect(8)
        assertThat(viewModel.characters.value).isEqualTo(chars('g', 'h', null, null))
        assertThat(viewModel.proposition.value).isEqualTo("MN")
        // Propose -> false
        viewModel.propose()
        verifyOnce(puzzle).propose(1, listOf(2, 3))
        assertThat(viewModel.activeGroup.value).isEqualTo(groups[1])
        assertThat(viewModel.proposition.value).isEqualTo("")
        assertThat(viewModel.characters.value).isEqualTo(chars('g', 'h', 'm', 'n'))
        // Propose -> true
        val words0 = verse.words.toMutableList()
        words0[4] = words0[4].resolvedVersion
        verse = verse.copy(words = words0)
        groups = listOf(
            groups[0],
            groups[1].copy(chars = chars(null, null, 'm', 'n'))
        )
        proposeResult = true
        clearInvocations(puzzle)
        viewModel.onCharSelect(0)
        viewModel.onCharSelect(1)
        assertThat(viewModel.characters.value).isEqualTo(chars(null, null, 'm', 'n'))
        viewModel.propose()
        verifyOnce(puzzle).propose(1, listOf(0, 1))
        assertThat(viewModel.activeGroup.value).isEqualTo(groups[1])
        assertThat(viewModel.words.value).isEqualTo(words0)
        // Select & cancel
        viewModel.onCharSelect(2)
        assertThat(viewModel.characters.value).isEqualTo(chars(null, null, null, 'n'))
        viewModel.cancel()
        assertThat(viewModel.characters.value).isEqualTo(chars(null, null, 'm', 'n'))
        // Resolve group
        clearInvocations(puzzle)
        groups = listOf(
            groups[0],
            groups[1].copy(resolved = true, chars = chars(null, null, null, null))
        )
        viewModel.onCharSelect(2)
        viewModel.onCharSelect(3)
        viewModel.propose()
        verifyOnce(puzzle).propose(1, listOf(2, 3))
        // Automatically goes to groups[0] as groups[1] is resolved
        assertThat(viewModel.activeGroup.value).isEqualTo(groups[0])
        assertThat(viewModel.proposition.value).isEqualTo("")
        assertThat(viewModel.characters.value).isEqualTo(chars('a', 'b', 'c'))
        // Complete game
        completed = true
        groups = listOf(
            groups[0].copy(chars = chars(null, null, 'c')),
            groups[1]
        )
        viewModel.onCharSelect(0)
        viewModel.onCharSelect(1)
        viewModel.propose()
        verifyOnce(puzzle).propose(0, listOf(0, 1))
        verifyOnce(puzzleVM).onComplete()
    }

    @Test
    fun bonus() {
        val verse = BibleVerse.fromText("", 1, 1, "Abc de")
        val random: Random = mock {
            onGeneric { from(any<List<Int>>()) } doAnswer {
                (it.arguments[0] as List<Int>)[0]
            }
        }
        val groups = listOf(
            HiddenWordsGroup(chars('d', 'e'), verse.words[0])
        )
        val puzzle = HiddenWordsPuzzleImpl(verse, groups, random)
        val puzzleLD: LiveData<Puzzle?> = MutableLiveData(puzzle)
        var canUseBonus = false
        val puzzleVM: PuzzleViewModel = mock {
            on { this.puzzle } doReturn puzzleLD
            on { canUseBonusOne() } doAnswer { canUseBonus }
        }
        val vm = HiddenWordsViewModel(puzzleVM, TestDispatchers)
        // not avail
        val words = verse.words.toList()
        vm.useBonusOne()
        assertThat(vm.words.value).isEqualTo(words)
        // Avail
        canUseBonus = true
        vm.useBonusOne()
        assertThat(vm.words.value!![2].unresolvedChar()).isEqualTo(listOf(1))
    }
}
