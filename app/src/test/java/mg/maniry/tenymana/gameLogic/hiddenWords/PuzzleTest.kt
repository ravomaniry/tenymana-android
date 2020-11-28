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
    fun untilComplete() {
        val verse = BibleVerse.fromText("", 1, 1, "ab cd efg hi jklmn o ba ba")
        val groups = listOf(
            HiddenWordsGroup(chars('a', 'b', 'c', 'd'), verse.words[4]),
            HiddenWordsGroup(chars('h', 'i', 'o', 'b', 'a'), verse.words[8])
        )
        val puzzle = HiddenWordsPuzzleImpl(verse, groups)
        // propose false
        assertThat(puzzle.propose(0, listOf(0, 2))).isFalse()
        assertThat(puzzle.completed).isFalse()
        // propose true
        val groups0 = listOf(
            groups[0].copy(chars = chars('a', 'b', null, null)),
            groups[1].copy(groups[1].chars.toList())
        )
        val didUpdate0 = puzzle.propose(0, listOf(2, 3))
        val words0 = verse.words.toMutableList()
        words0[2] = words0[2].resolvedVersion
        assertThat(didUpdate0).isTrue()
        assertThat(puzzle.groups).isEqualTo(groups0)
        assertThat(puzzle.score.value).isEqualTo(2)
        assertThat(puzzle.verse.words).isEqualTo(words0)
        // Propose all shown words + not original chars -> reveal hidden words + increment score
        val words1 = words0.toMutableList()
        words1[4] = words1[4].resolvedVersion
        words1[12] = words0[12].resolvedVersion
        words1[14] = words0[14].resolvedVersion
        val didUpdate1 = puzzle.propose(0, listOf(1, 0))
        assertThat(didUpdate1).isTrue()
        assertThat(puzzle.verse.words).isEqualTo(words1)
        assertThat(puzzle.score.value).isEqualTo(2 + 2 + 2 + 3)
    }
}
