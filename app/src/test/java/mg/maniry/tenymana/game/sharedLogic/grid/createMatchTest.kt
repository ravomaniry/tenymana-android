package mg.maniry.tenymana.game.sharedLogic.grid

import com.google.common.truth.Truth
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import mg.maniry.tenymana.game.linkClear.LinkClearBoard.Companion.direction
import mg.maniry.tenymana.game.linkClear.LinkClearBoard.Companion.gravity
import mg.maniry.tenymana.game.models.*
import mg.maniry.tenymana.utils.Random
import org.junit.Test

class CreateMatchTest {
    @Test
    fun createMatch_singleWord() {
        val words = BibleVerse.fromText("", 1, 1, "Abc de").words
        testCreateMatch(
            words = words,
            visbleH = 4,
            randomIndex = 2,
            cells = listOf(
                listOf(c('a'), c('d'), c('b'), c('e'), c('c')),
                listOf(null, null, null, null, null)
            ),
            diff = listOf(Move.xy(1, 1, 1, 0), Move.xy(0, 1, 0, 0), Move.xy(2, 1, 2, 0)),
            cellsRes = listOf(
                listOf(c('a'), c('b'), c('c'), null, null),
                listOf(null, null, null, null, null)
            ),
            // newDiff = [(2, 0, 1, 0), (4, 0, 2, 0)]
            // cleared = [(1, 0), (3, 0)]
            diffRes = listOf(Move.xy(0, 1, 0, 0), Move.xy(2, 1, 1, 0), Move.xy(4, 0, 2, 0)),
            revealed = 2
        )
    }

    private fun testCreateMatch(
        words: List<Word>,
        diff: List<Move>,
        randomIndex: Int,
        visbleH: Int,
        cells: List<List<Character?>>,
        revealed: Int?,
        cellsRes: List<List<Character?>>,
        diffRes: List<Move>
    ) {
        val random: Random = mock { on { int(any(), any()) } doReturn randomIndex }
        val grid = Grid(cells).toMutable()
        val mWords = words.toMutableList()
        val mDiff = diff.toMutableList()
        val i = grid.createMatch(mWords, mDiff, visbleH, direction, gravity, random)
        Truth.assertThat(grid.toGrid()).isEqualTo(Grid(cellsRes))
        Truth.assertThat(i).isEqualTo(revealed)
        Truth.assertThat(mDiff).isEqualTo(diffRes)
    }

    private fun c(v: Char) = Character(v.toUpperCase(), v.toLowerCase())
}