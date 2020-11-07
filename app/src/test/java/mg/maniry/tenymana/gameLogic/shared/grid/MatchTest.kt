package mg.maniry.tenymana.gameLogic.shared.grid

import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.mock
import mg.maniry.tenymana.gameLogic.linkClear.LinkClearPuzzle
import mg.maniry.tenymana.gameLogic.models.BibleVerse
import mg.maniry.tenymana.gameLogic.models.Grid
import mg.maniry.tenymana.gameLogic.models.Move
import mg.maniry.tenymana.gameLogic.models.Point
import mg.maniry.tenymana.gameLogic.models.Point.Companion.directions
import mg.maniry.tenymana.utils.Random
import mg.maniry.tenymana.utils.chars
import mg.maniry.tenymana.utils.verifyOnce
import org.junit.Test

class MatchTest {
    @Test
    fun left_right() {
        val words = BibleVerse.fromText("Matio", 1, 1, "Abc de fg ij").words.toMutableList()
        val grid = Grid(
            listOf(
                chars('a', 'b', 'c'),
                chars('a', 'e', 'd'),
                chars('g', 'f', null)
            )
        )
        assertThat(grid.firstVisibleMatch(words, 2, directions)).isEqualTo(Move.xy(0, 0, 2, 0))
        // [0] is resolved
        words[0] = words[0].resolvedVersion
        assertThat(grid.firstVisibleMatch(words, 2, directions)).isEqualTo(Move.xy(2, 1, 1, 1))
        // [0], [2] resolved + off grid
        words[2] = words[2].resolvedVersion
        assertThat(grid.firstVisibleMatch(words, 2, directions)).isNull()
        // [0], [2] resolved on grid
        assertThat(grid.firstVisibleMatch(words, 3, directions)).isEqualTo(Move.xy(1, 2, 0, 2))
        // [0], [2], [4] resolved
        words[4] = words[4].resolvedVersion
        assertThat(grid.firstVisibleMatch(words, 4, directions)).isNull()
    }

    @Test
    fun upDown_gridH_limit() {
        val words = BibleVerse.fromText("", 1, 1, "Abc de fg").words
        val grid = Grid(
            listOf(
                chars('a', 'e'),
                chars('b', 'd'),
                chars('c', null)
            )
        )
        // ABC off grid
        assertThat(grid.firstVisibleMatch(words, 2, directions)).isEqualTo(Move.xy(1, 1, 1, 0))
        // ABC in grid
        assertThat(grid.firstVisibleMatch(words, 3, directions)).isEqualTo(Move.xy(0, 0, 0, 2))
    }

    @Test
    fun randomMatch() {
        val words = BibleVerse.fromText("", 1, 1, "Abc de fg").words.toMutableList()
        val grid = Grid(
            listOf(
                chars('a', 'e'),
                chars('b', 'd'),
                chars('c', null)
            )
        )
        var randomIndex = 0
        val random: Random = mock {
            on { int(any(), any()) } doAnswer { randomIndex }
        }
        // random is near 3
        randomIndex = 2
        assertThat(grid.randomMatch(words, 4, LinkClearPuzzle.directions, random))
            .isEqualTo(listOf(Point(1, 1), Point(1, 0)))
        verifyOnce(random).int(0, 5)
        // random is near 0
        randomIndex = 5
        assertThat(grid.randomMatch(words, 4, LinkClearPuzzle.directions, random))
            .isEqualTo(listOf(Point(0, 0), Point(0, 1), Point(0, 2)))
        // random is 0 but not visible
        randomIndex = 0
        assertThat(grid.randomMatch(words, 2, LinkClearPuzzle.directions, random))
            .isEqualTo(listOf(Point(1, 1), Point(1, 0)))
        verifyOnce(random).int(0, 3)
        // random is 0 but already resolved
        words[0] = words[0].resolvedVersion
        randomIndex = 0
        assertThat(grid.randomMatch(words, 4, LinkClearPuzzle.directions, random))
            .isEqualTo(listOf(Point(1, 1), Point(1, 0)))
        // no more match so null
        words[2] = words[2].resolvedVersion
        assertThat(grid.randomMatch(words, 4, LinkClearPuzzle.directions, random)).isNull()
    }
}
