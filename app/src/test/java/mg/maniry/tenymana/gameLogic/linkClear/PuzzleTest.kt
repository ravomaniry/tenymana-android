package mg.maniry.tenymana.gameLogic.linkClear

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import mg.maniry.tenymana.gameLogic.models.*
import org.junit.Rule
import org.junit.Test

class PuzzleTest {
    @get:Rule
    val rule = InstantTaskExecutorRule()

    // | D
    // | E | I | J | . |
    // | A | B | C | . |
    private fun grid() = Grid(
        listOf(
            listOf(ca(0, 0), ca(0, 1), ca(0, 2), null),
            listOf(ca(2, 1), ca(8, 0), ca(8, 1), null),
            listOf(ca(2, 0), null, null, null),
            listOf(null, null, null, null)
        )
    )

    @Test
    fun wrongReponses() {
        val verse = BibleVerse.fromText("Matio", 1, 1, "Abc de àbc fghi, ij")
        val board = LinkClearPuzzleImpl(grid(), verse)
        val lines = listOf(
            Move.xy(0, 0, 2, 1),
            Move.xy(0, 0, 0, 1),
            Move.xy(0, 0, 1, 1),
            Move.xy(0, 0, 0, 2),
            Move.xy(2, 1, 1, 1),
            Move.xy(3, 0, 0, 0),
            Move.xy(0, 2, 0, 0)
        )
        val w0 = board.verse.words.map { it.copy() }
        for (l in lines) {
            testPropose(board, l, w0)
            assertThat(board.score).isEqualTo(0)
        }
    }

    @Test
    fun adjust() {
        val verse = BibleVerse.fromText("Matio", 1, 1, "Abc de àbc fghi, ij")
        val puzzle = LinkClearPuzzleImpl(grid(), verse)
        testPropose(
            puzzle,
            move = Move.xy(-2, 0, 5, 0),
            words = puzzle.verse.words,
            didUpdate = true,
            cleared = listOf(Point(0, 0), Point(1, 0), Point(2, 0)),
            diff = listOf(
                Move.xy(0, 1, 0, 0),
                Move.xy(0, 2, 0, 1),
                Move.xy(2, 1, 2, 0),
                Move.xy(1, 1, 1, 0)
            )
        )
        testPropose(
            puzzle,
            move = Move.xy(0, 20, 0, -5),
            words = puzzle.verse.words,
            didUpdate = true,
            cleared = listOf(Point(0, 1), Point(0, 0)),
            diff = listOf(Move.xy(1, 0, 0, 0), Move.xy(2, 0, 1, 0))
        )
    }

    @Test
    fun basic() {
        val verse = BibleVerse.fromText("Matio", 1, 1, "Abc de àbc fghi, ij")
        val puzzle = LinkClearPuzzleImpl(grid(), verse)
        // Resolve rows[0]: words[0] && words[4]
        val w1 = puzzle.verse.words.toMutableList()
        w1[0] = w1[0].resolvedVersion
        w1[4] = w1[4].resolvedVersion
        w1[6] = w1[6].resolvedVersion // automatically resolved
        testPropose(
            board = puzzle,
            move = Move.xy(0, 0, 2, 0),
            words = w1,
            didUpdate = true,
            cleared = listOf(Point(0, 0), Point(1, 0), Point(2, 0)),
            diff = listOf(
                Move.xy(0, 1, 0, 0),
                Move.xy(0, 2, 0, 1),
                Move.xy(2, 1, 2, 0),
                Move.xy(1, 1, 1, 0)
            )
        )
        assertThat(puzzle.score).isEqualTo(10)
        // Invalid move
        testPropose(puzzle, Move.xy(0, 0, 1, 0), w1)
        // Resolve words[8]
        val w2 = w1.toMutableList()
        w2[8] = w2[8].resolvedVersion
        testPropose(
            puzzle,
            move = Move.xy(1, 0, 3, 0),
            words = w2,
            didUpdate = true,
            cleared = listOf(Point(1, 0), Point(2, 0)),
            diff = listOf()
        )
        assertThat(puzzle.score).isEqualTo(12)
        // Resolve last word: words[2]
        val w3 = w2.toMutableList()
        w3[2] = w3[2].resolvedVersion
        testPropose(
            puzzle,
            Move.xy(0, 2, 0, 0),
            w3,
            didUpdate = true,
            cleared = listOf(Point(0, 1), Point(0, 0)),
            diff = listOf(),
            completed = true
        )
        assertThat(puzzle.score).isEqualTo(14 * 2) // total = 10 * (resolved words = 2)
    }

    @Test
    fun noPossibleMove_createMatch() {
        val verse = BibleVerse.fromText("Matio", 1, 1, "Ab cde fghi")
        // | I |   |   |   |     |   |   |   |   |     |   |   |   |   |
        // | C | F | G | H | ==> | I | G | H |   | ==> |   |   |   |   |
        // | A | B | D | E |     | C | F | D | E |     | C | D | E |   |
        val grid = Grid(
            listOf(
                listOf(ca(0, 0), ca(0, 1), ca(2, 1), ca(2, 2)),
                listOf(ca(2, 0), ca(4, 0), ca(4, 1), ca(4, 2)),
                listOf(ca(4, 3), null, null, null)
            )
        )
        val board = LinkClearPuzzleImpl(grid, verse)
        board.propose(Move.xy(0, 0, 1, 0))
        assertThat(board.cleared).isEqualTo(
            listOf(
                Point(0, 0),
                Point(1, 0),
                Point(0, 1),
                Point(1, 1),
                Point(2, 1)
            )
        )
        assertThat(board.grid).isEqualTo(
            MutableGrid(
                4,
                mutableListOf<MutableList<Character?>>(
                    mutableListOf(c('c'), c('d'), c('e'), null),
                    mutableListOf(null, null, null, null),
                    mutableListOf(null, null, null, null),
                    mutableListOf(null, null, null, null)
                )
            )
        )
        assertThat(board.diff).isEqualTo(
            listOf(
                Move.xy(0, 1, 0, 0),
                Move.xy(3, 0, 2, 0),
                Move.xy(2, 0, 1, 0)
            )
        )
        assertThat(board.score).isEqualTo(2)
    }

    @Test
    fun noMoreMove_complete() {
        val verse = BibleVerse.fromText("", 1, 1, "Abc def ghi jk")
        // | I | F | H |   |     | I |   |   |   |
        // | G | J | K | C | ==> | G | F | H | C |
        // | A | B | D | E |     | A | B | D | E |
        val grid = Grid(
            listOf(
                listOf(ca(0, 0), ca(0, 1), ca(2, 0), ca(2, 1)),
                listOf(ca(4, 0), ca(6, 0), ca(6, 1), ca(0, 2)),
                listOf(ca(4, 2), ca(2, 2), ca(4, 1), null),
                listOf(null, null, null, null)
            )
        )
        val board = LinkClearPuzzleImpl(grid, verse)
        board.propose(Move.xy(1, 1, 2, 1))
        assertThat(board.completed).isTrue()
        assertThat(board.score).isEqualTo(2)
    }

    private fun testPropose(
        board: LinkClearPuzzle,
        move: Move,
        words: List<Word>,
        didUpdate: Boolean = false,
        cleared: List<Point>? = null,
        diff: List<Move>? = null,
        completed: Boolean = false
    ) {
        val resp = board.propose(move)
        assertThat(resp).isEqualTo(didUpdate)
        assertThat(board.verse.words).isEqualTo(words)
        assertThat(board.diff).isEqualTo(diff)
        assertThat(board.cleared).isEqualTo(cleared)
        assertThat(board.completed).isEqualTo(completed)
    }

    private fun ca(wI: Int, cI: Int) = CharAddress(wI, cI)
    private fun c(v: Char) = Character(v.toUpperCase(), v.toLowerCase())
}
