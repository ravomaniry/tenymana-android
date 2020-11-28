package mg.maniry.tenymana.gameLogic.hiddenWords

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import mg.maniry.tenymana.gameLogic.models.BibleVerse
import mg.maniry.tenymana.utils.chars
import org.junit.Rule
import org.junit.Test

class PuzzleTest {
    @get:Rule
    val liveDataRule = InstantTaskExecutorRule()

    @Test
    fun propose_to_success() {
        val verse = BibleVerse.fromText("", 1, 1, "ab cd efg hi jklmn o ba ba")
        val groups = listOf(
            HiddenWordsGroup(chars('a', 'b', 'c', 'd'), verse.words[4]),
            HiddenWordsGroup(chars('h', 'i', 'o', 'b', 'a'), verse.words[8])
        )
        val puzzle = HiddenWordsPuzzleImpl(verse, groups)
        var score = 0
        // firstGroup default to 0
        assertThat(puzzle.firstGroup).isEqualTo(0)
        // propose false
        assertThat(puzzle.propose(0, listOf(0, 2))).isFalse()
        assertThat(puzzle.completed).isFalse()
        assertThat(puzzle.firstGroup).isEqualTo(0)
        // propose CD true
        score += 2
        val groups0 = listOf(
            groups[0].copy(chars = chars('a', 'b', null, null)),
            groups[1].copy(groups[1].chars.toList())
        )
        val didUpdate0 = puzzle.propose(0, listOf(2, 3))
        val words0 = verse.words.toMutableList()
        words0[2] = words0[2].resolvedVersion
        assertThat(didUpdate0).isTrue()
        assertThat(puzzle.groups).isEqualTo(groups0)
        assertThat(puzzle.score.value).isEqualTo(score)
        assertThat(puzzle.verse.words).isEqualTo(words0)
        assertThat(puzzle.completed).isFalse()
        assertThat(puzzle.firstGroup).isEqualTo(0)
        // Propose BA all shown words + not original chars -> reveal hidden words + increment score
        score += 2 * 2 + 3
        val words1 = words0.toMutableList()
        words1[4] = words1[4].resolvedVersion
        words1[12] = words0[12].resolvedVersion
        words1[14] = words0[14].resolvedVersion
        val didUpdate1 = puzzle.propose(0, listOf(1, 0))
        assertThat(didUpdate1).isTrue()
        assertThat(puzzle.verse.words).isEqualTo(words1)
        assertThat(puzzle.score.value).isEqualTo(score)
        assertThat(puzzle.completed).isFalse()
        assertThat(puzzle.groups[0].resolved).isTrue()
        assertThat(puzzle.firstGroup).isEqualTo(1)
        // Propose group 1 until complete
        // O
        score += 1
        val didUpdate2 = puzzle.propose(1, listOf(2))
        assertThat(didUpdate2).isTrue()
        assertThat(puzzle.score.value).isEqualTo(score)
        assertThat(puzzle.completed).isFalse()
        assertThat(puzzle.verse.words[10].resolved).isTrue()
        assertThat(puzzle.groups[1].chars).isEqualTo(chars('h', 'i', null, 'b', 'a'))
        assertThat(puzzle.firstGroup).isEqualTo(1)
        // HI
        score += 2
        val didUpdate3 = puzzle.propose(1, listOf(0, 1))
        assertThat(didUpdate3).isTrue()
        assertThat(puzzle.score.value).isEqualTo(score)
        assertThat(puzzle.completed).isFalse()
        assertThat(puzzle.verse.words[6].resolved).isTrue()
        assertThat(puzzle.groups[1]).isEqualTo(
            groups[1].copy(chars = chars(null, null, null, 'b', 'a'))
        )
        // AB -> show hidden wods -> complete puzzle
        score += 2 + 5
        score *= 2
        val didUpdate4 = puzzle.propose(1, listOf(4, 3))
        assertThat(didUpdate4).isTrue()
        assertThat(puzzle.score.value).isEqualTo(score)
        assertThat(puzzle.completed).isTrue()
        for (w in puzzle.verse.words) {
            assertThat(w.resolved).isTrue()
        }
        assertThat(puzzle.groups).isEqualTo(
            listOf(
                groups[0].copy(
                    chars = chars(null, null, null, null),
                    resolved = true
                ),
                groups[1].copy(
                    chars = chars(null, null, null, null, null),
                    resolved = true
                )
            )
        )
    }

    @Test
    fun propose_then_noMatch() {
        val verse = BibleVerse.fromText("", 1, 1, "Abc def ab defg")
        val groups = listOf(
            HiddenWordsGroup(chars('a', 'b', 'c'), verse.words[2]),
            HiddenWordsGroup(chars('a', 'b'), verse.words[6])
        )
        val puzzle = HiddenWordsPuzzleImpl(verse, groups)
        assertThat(puzzle.firstGroup).isEqualTo(0)
        // Propose abc of groups[0] -> reveal and complete
        val didUpdate = puzzle.propose(0, listOf(0, 1))
        assertThat(didUpdate).isTrue()
        assertThat(puzzle.score.value).isEqualTo(2)
        assertThat(puzzle.completed).isTrue()
    }
}
