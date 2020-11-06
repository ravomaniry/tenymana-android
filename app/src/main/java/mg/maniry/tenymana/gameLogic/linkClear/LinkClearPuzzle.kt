package mg.maniry.tenymana.gameLogic.linkClear

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import mg.maniry.tenymana.gameLogic.models.*
import mg.maniry.tenymana.gameLogic.shared.grid.*
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
    val diff: List<Move>?
    val cleared: LiveData<List<Point>?>
    val solution: List<SolutionItem<Character>>

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

    private val words = initialVerse.words.toMutableList()
    override val verse = initialVerse.copy(words = words)
    override var diff: List<Move>? = null
    private var _cleared: List<Point>? = null
    override val cleared = MutableLiveData<List<Point>?>()

    private var usedHelp = false
    override var completed = false
    override var score = MutableLiveData(0)

    override fun propose(move: Move): Boolean {
        reset()
        val selection = grid.calcSelection(move, directions)
        if (selection.isNotEmpty) {
            val historyItem = HistoryItem(words.toList(), score.value!!, grid.copyCells())
            val indexes = words.resolveWith(selection.chars, hidden)
            if (indexes.isNotEmpty()) {
                updateResult(selection.points)
                incrementScore(indexes)
                syncLiveData()
                history.add(historyItem)
                history.trimLeft(LinkClearPuzzle.historySize)
                return true
            }
        }
        syncLiveData()
        return false
    }

    override fun undo(): Boolean {
        if (history.isEmpty()) {
            return false
        }
        val item = history.last()
        score.postValue(item.score)
        item.words.overWrite(words)
        item.chars.overWrite(grid)
        history.removeLastIfNotEmpty()
        return true
    }

    private fun reset() {
        completed = false
        _cleared = null
        diff = null
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
                diff = match.diff
                words[match.word] = words[match.word].resolvedVersion
                completed = words.resolved
                _cleared = points.toMutableList().apply { addAll(match.cleared) }.toSet().toList()
            }
        } else {
            diff = diff0
            _cleared = points
        }
    }

    private fun incrementScore(resolved: List<Int>) {
        resolved.forEach {
            score.postValue((score.value ?: 0) + words[it].size)
        }
        if (completed && !usedHelp) {
            score.postValue((score.value ?: 0) * 2)
        }
    }

    private fun syncLiveData() {
        if (_cleared != cleared.value) {
            cleared.postValue(_cleared)
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
