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
        val board = LinkClearPuzzleImpl(grid(), verse, emptyList())
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
            assertThat(board.score.value).isEqualTo(0)
        }
    }

    @Test
    fun adjust() {
        val verse = BibleVerse.fromText("Matio", 1, 1, "Abc de àbc fghi, ij")
        val puzzle = LinkClearPuzzleImpl(grid(), verse, emptyList())
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
        val puzzle = LinkClearPuzzleImpl(grid(), verse, emptyList())
        // Resolve rows[0]: words[0] && words[4]
        val w1 = puzzle.verse.words.toMutableList()
        w1[0] = w1[0].resolvedVersion
        w1[4] = w1[4].resolvedVersion
        w1[6] = w1[6].resolvedVersion // automatically resolved
        testPropose(
            puzzle = puzzle,
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
        assertThat(puzzle.score.value).isEqualTo(10)
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
        assertThat(puzzle.score.value).isEqualTo(12)
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
        assertThat(puzzle.score.value).isEqualTo(14 * 2) // total = 10 + 10 * 1
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
        val board = LinkClearPuzzleImpl(grid, verse, emptyList())
        board.propose(Move.xy(0, 0, 1, 0))
        assertThat(board.cleared.value).isEqualTo(
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
        assertThat(board.diffs.value).isEqualTo(
            listOf(
                Move.xy(0, 1, 0, 0),
                Move.xy(3, 0, 2, 0),
                Move.xy(2, 0, 1, 0)
            )
        )
        assertThat(board.score.value).isEqualTo(2)
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
        val puzzle = LinkClearPuzzleImpl(grid, verse, emptyList())
        puzzle.propose(Move.xy(1, 1, 2, 1))
        assertThat(puzzle.completed).isTrue()
        assertThat(puzzle.score.value).isEqualTo(2)
    }

    @Test
    fun noDiagonalMove() {
        // | C | B |
        // | A |   |
        val verse = BibleVerse.fromText("", 1, 1, "Ab c")
        val grid = Grid(
            listOf(
                listOf(ca(0, 0), null),
                listOf(ca(2, 0), ca(0, 1))
            )
        )
        val puzzle = LinkClearPuzzleImpl(grid, verse, emptyList())
        testPropose(puzzle, Move.xy(0, 0, 1, 1), verse.words)
    }

    private fun testPropose(
        puzzle: LinkClearPuzzle,
        move: Move,
        words: List<Word>,
        didUpdate: Boolean = false,
        cleared: List<Point>? = null,
        diff: List<Move>? = null,
        completed: Boolean = false
    ) {
        val resp = puzzle.propose(move)
        assertThat(resp).isEqualTo(didUpdate)
        assertThat(puzzle.verse.words).isEqualTo(words)
        assertThat(puzzle.diffs.value).isEqualTo(diff)
        assertThat(puzzle.cleared.value).isEqualTo(cleared)
        assertThat(puzzle.completed).isEqualTo(completed)
    }

    @Test
    fun history() {
        val verse = BibleVerse.fromText("", 1, 1, "ab cd ef gh ij kl mn op qr st uv xy za")
        val cells = mutableListOf<List<CharAddress>>()
        verse.words.forEachIndexed { i, word ->
            if (!word.isSeparator) {
                cells.add(listOf(ca(i, 0), ca(i, 1)))
            }
        }
        val grid = Grid(cells)
        val puzzle = LinkClearPuzzleImpl(grid, verse, emptyList())
        // propose word[0] and cancel
        var prevGrid = puzzle.grid.toGrid()
        val wSnap = puzzle.verse.words.snapshot
        val gridSnapshot = puzzle.grid.snapshot
        puzzle.propose(Move.xy(0, 0, 1, 0))
        assertThat(puzzle.verse.words[0].resolved).isTrue()
        assertThat(puzzle.score.value).isEqualTo(2)
        assertThat(puzzle.prevGrid.snapshot).isEqualTo(prevGrid.snapshot)
        // cancel
        prevGrid = puzzle.grid.toGrid()
        var canceled = puzzle.undo()
        assertThat(canceled).isTrue()
        assertThat(puzzle.verse.words.snapshot).isEqualTo(wSnap)
        assertThat(puzzle.score.value).isEqualTo(0)
        assertThat(puzzle.grid.snapshot).isEqualTo(gridSnapshot)
        assertThat(puzzle.prevGrid.snapshot).isEqualTo(prevGrid.snapshot)
        // cancel now do nothing
        canceled = puzzle.undo()
        assertThat(canceled).isFalse()
        assertThat(puzzle.score.value).isEqualTo(0)
        assertThat(puzzle.prevGrid.snapshot).isEqualTo(prevGrid.snapshot)
        // Propose 5 times
        val wordsSnapthots = mutableListOf<String>()
        val gridSnapshots = mutableListOf<String>()
        val scoreSnapthots = mutableListOf<Int?>()
        for (i in 0..11) {
            val changed = puzzle.propose(Move.xy(0, 0, 1, 0))
            assertThat(changed).isTrue()
            assertThat(puzzle.verse.words[i * 2].resolved).isTrue()
            wordsSnapthots.add(puzzle.verse.words.snapshot)
            gridSnapshots.add(puzzle.grid.snapshot)
            scoreSnapthots.add(puzzle.score.value)
        }
        // cancel
        for (i in 0..9) {
            val changed = puzzle.undo()
            assertThat(changed).isTrue()
            assertThat(puzzle.verse.words.snapshot).isEqualTo(wordsSnapthots[10 - i])
            assertThat(puzzle.score.value).isEqualTo(scoreSnapthots[10 - i])
            assertThat(puzzle.grid.snapshot).isEqualTo(gridSnapshots[10 - i])
        }
        assertThat(puzzle.undo()).isFalse()
    }

    @Test
    fun useBonusOne() {
        val words = BibleVerse.fromText("", 1, 1, "Ab").words.toMutableList()
        val cells = listOf(
            listOf(ca(0, 0), ca(0, 1))
        )
        val grid = Grid(cells)
        val verse = BibleVerse("", 1, 1, words.joinToString { "${it.value} " }, words)
        val puzzle = LinkClearPuzzleImpl(grid, verse, emptyList())
        // use bonus once
        assertThat(puzzle.useBonusOne(10)).isEqualTo(listOf(Point(0, 0)))
        assertThat(puzzle.score.value).isEqualTo(-10)
        // use bonus but not avail (not real case)
        (puzzle.verse.words as MutableList).apply { this[0] = this[0].resolvedVersion }
        assertThat(puzzle.useBonusOne(10)).isNull()
        assertThat(puzzle.score.value).isEqualTo(-10)
    }

    private val Grid<*>.snapshot: String get() = toString().trimNulls()
    private val List<Word>.snapshot: String get() = joinToString { "$it\n" }

    private fun String.trimNulls(): String {
        return split('\n')
            .filter { !it.allElementsAreNull() }
            .joinToString { "$it\n" }
    }

    private fun String.allElementsAreNull(): Boolean {
        val elements = substring(1, length - 1).split(", ")
        return elements.find { it != "null" } == null
    }

    private fun ca(wI: Int, cI: Int) = CharAddress(wI, cI)
    private fun c(v: Char) = Character(v.toUpperCase(), v.toLowerCase())
}
