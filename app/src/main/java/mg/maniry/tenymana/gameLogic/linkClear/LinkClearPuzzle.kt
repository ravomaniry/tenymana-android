package mg.maniry.tenymana.gameLogic.linkClear

import androidx.lifecycle.MutableLiveData
import mg.maniry.tenymana.gameLogic.models.*
import mg.maniry.tenymana.gameLogic.shared.grid.*
import mg.maniry.tenymana.gameLogic.shared.words.bonusRatio
import mg.maniry.tenymana.gameLogic.shared.words.resolveWith
import mg.maniry.tenymana.gameLogic.shared.words.resolved
import mg.maniry.tenymana.utils.Random
import mg.maniry.tenymana.utils.RandomImpl

data class SolutionItem<T>(
    val grid: Grid<T>,
    val points: List<Point>
)

interface LinkClearPuzzle : Puzzle {
    val grid: Grid<Character>
    val diffs: List<Move>?
    val cleared: List<Point>?
    val solution: List<SolutionItem<Character>>
    val prevGrid: Grid<Character>

    companion object {
        const val gridSize = 10
        const val historySize = 10
        val gravity = listOf(Point.DOWN, Point.LEFT)
        val directions = listOf(Point.UP, Point.RIGHT, Point.DOWN, Point.LEFT)

        fun build(verse: BibleVerse, random: Random): LinkClearPuzzle {
            val result = buildLinkGrid(verse, random, gridSize, gridSize)
            val solution = result.second.map { it.toCharSolution(verse) }
            return LinkClearPuzzleImpl(result.first, verse, solution)
        }
    }
}

class LinkClearPuzzleImpl(
    initialGrid: Grid<CharAddress>,
    initialVerse: BibleVerse,
    override val solution: List<SolutionItem<Character>>
) : LinkClearPuzzle {
    private val visibleH = LinkClearPuzzle.gridSize
    private val gravity = LinkClearPuzzle.gravity
    private val directions = LinkClearPuzzle.directions

    private val random = RandomImpl()
    private val history = mutableListOf<HistoryItem>()

    private val hidden = initialGrid.calcHiddenWords(initialVerse.words)
    override val grid: MutableGrid<Character> = initialGrid.toCharGrid(initialVerse.words)
    private val _prevGrid = grid.toMutable()
    override val prevGrid: Grid<Character> get() = _prevGrid
    private var prevCells: List<List<Character?>> = grid.cells

    private val words = initialVerse.words.toMutableList()
    override val verse = initialVerse.copy(words = words)
    private var _diffs: List<Move>? = null
    override val diffs: List<Move>? get() = _diffs
    override var cleared: List<Point>? = null

    private var usedHelp = false
    override var completed = false
    override var score = MutableLiveData(0)
    private val scoreValue: Int get() = score.value ?: 0

    override fun propose(move: Move): Boolean {
        reset()
        takeGridSnapshot()
        val selection = grid.calcSelection(move, directions)
        if (selection.isNotEmpty) {
            val historyItem = HistoryItem(words.toList(), score.value!!, grid.copyCells())
            val indexes = words.resolveWith(selection.chars, hidden)
            if (indexes.isNotEmpty()) {
                updateResult(selection.points)
                incrementScore(indexes)
                updatePrevGrid()
                history.add(historyItem)
                history.trimLeft(LinkClearPuzzle.historySize)
                return true
            }
        }
        return false
    }

    override fun undo(): Boolean {
        if (history.isEmpty()) {
            return false
        }
        takeGridSnapshot()
        updatePrevGrid()
        val item = history.last()
        score.postValue(item.score)
        item.words.overWrite(words)
        item.chars.overWrite(grid)
        history.removeLastIfNotEmpty()
        return true
    }

    override fun useBonusOne(price: Int): List<Point>? {
        val points = grid.randomMatch(words, visibleH, directions, random)
        return points?.let {
            score.postValue(scoreValue - price)
            listOf(points[0])
        }
    }

    private fun takeGridSnapshot() {
        prevCells = grid.copyCells()
    }

    private fun updatePrevGrid() {
        prevCells.overWrite(_prevGrid)
    }

    private fun reset() {
        completed = false
        cleared = null
        _diffs = null
    }

    private fun updateResult(points: List<Point>) {
        val diff0 = grid.clear(points, gravity)
        completed = words.resolved
        if (!completed && grid.firstVisibleMatch(words, visibleH, directions) == null) {
            usedHelp = true
            val match =
                grid.createMatch(words, diff0, visibleH, directions, gravity, random)
            if (match == null) {
                completed = true
            } else {
                _diffs = match.diff
                words[match.word] = words[match.word].resolvedVersion
                completed = words.resolved
                cleared = points.toMutableList().apply { addAll(match.cleared) }.toSet().toList()
            }
        } else {
            _diffs = diff0
            cleared = points
        }
    }

    private fun incrementScore(resolved: List<Int>) {
        resolved.forEach {
            score.postValue(scoreValue + words[it].size)
        }
        if (completed && !usedHelp) {
            score.postValue(scoreValue * (1.0 + words.bonusRatio).toInt())
        }
    }
}

private class HistoryItem(
    val words: List<Word>,
    val score: Int,
    val chars: List<List<Character?>>
)

private fun List<Word>.overWrite(words: MutableList<Word>) {
    forEachIndexed { index, word -> words[index] = word }
}

private fun MutableList<HistoryItem>.trimLeft(len: Int) {
    while (size > len) {
        removeAt(0)
    }
}

private fun MutableList<HistoryItem>.removeLastIfNotEmpty() {
    if (size > 0) {
        removeAt(size - 1)
    }
}

private fun Grid<Character>.copyCells(): List<List<Character?>> {
    return cells.map { it.toList() }
}

private fun List<List<Character?>>.overWrite(grid: MutableGrid<Character>) {
    for (y in indices) {
        for (x in this[y].indices) {
            grid.set(x, y, this[y][x])
        }
    }
}

private fun SolutionItem<CharAddress>.toCharSolution(verse: BibleVerse): SolutionItem<Character> {
    return SolutionItem(grid.toCharGrid(verse.words), points)
}
