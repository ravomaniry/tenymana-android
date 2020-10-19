package mg.maniry.tenymana.game.sharedLogic.grid

import com.google.common.truth.Truth
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import mg.maniry.tenymana.game.puzzles.linkClear.LinkClearPuzzle.Companion.direction
import mg.maniry.tenymana.game.puzzles.linkClear.LinkClearPuzzle.Companion.gravity
import mg.maniry.tenymana.game.models.*
import mg.maniry.tenymana.utils.Random
import org.junit.Test

class CreateMatchTest {
    @Test
    fun singleWord() {
        val words = BibleVerse.fromText("", 1, 1, "Abc de").words
        testCreateMatch(
            words = words,
            visbleH = 4,
            randomIndex = 2,
            cells = listOf(
                chars('a', 'd', 'b', 'e', 'c'),
                chars(null, null, null, null, null)
            ),
            diff = listOf(Move.xy(1, 1, 1, 0), Move.xy(0, 1, 0, 0), Move.xy(2, 1, 2, 0)),
            cellsRes = listOf(
                chars('a', 'b', 'c', null, null),
                chars(null, null, null, null, null)
            ),
            // newDiff = [(2, 0, 1, 0), (4, 0, 2, 0)]
            revealedWord = 2,
            clearedCells = listOf(Point(1, 0), Point(3, 0)),
            diffRes = listOf(Move.xy(0, 1, 0, 0), Move.xy(2, 1, 1, 0), Move.xy(4, 0, 2, 0))
        )
    }

    @Test
    fun notOrdered_notPickedFirst() {
        val words = BibleVerse.fromText("", 1, 1, "Abc def").words
        testCreateMatch(
            words,
            visbleH = 2,
            cells = listOf(
                chars('d', 'c', 'b', 'c'),
                chars('a', 'e', 'f', null),
                chars(null, null, null, null)
            ),
            randomIndex = 3,
            diff = listOf(Move.xy(0, 2, 0, 1), Move.xy(1, 2, 1, 1), Move.xy(2, 2, 2, 1)),
            revealedWord = 0,
            clearedCells = listOf(Point(0, 1), Point(1, 0), Point(2, 0)),
            diffRes = listOf(Move.xy(1, 2, 1, 0), Move.xy(2, 2, 2, 0)),
            cellsRes = listOf(
                chars('d', 'e', 'f', 'c'),
                chars(null, null, null, null),
                chars(null, null, null, null)
            )
        )
    }

    private fun testCreateMatch(
        words: List<Word>,
        visbleH: Int,
        randomIndex: Int,
        diff: List<Move>,
        cells: List<List<Character?>>,
        revealedWord: Int,
        clearedCells: List<Point>,
        cellsRes: List<List<Character?>>,
        diffRes: List<Move>
    ) {
        val random: Random = mock { on { int(any(), any()) } doReturn randomIndex }
        val grid = Grid(cells).toMutable()
        val mWords = words.toMutableList()
        val res = grid.createMatch(mWords, diff, visbleH, direction, gravity, random)
        Truth.assertThat(grid.toGrid()).isEqualTo(Grid(cellsRes))
        Truth.assertThat(res).isEqualTo(CreateMatchResult(revealedWord, clearedCells, diffRes))
    }

    private fun chars(vararg v: Char?): List<Character?> {
        return v.map {
            it?.let { Character(it.toUpperCase(), it.toLowerCase()) }
        }
    }
}
