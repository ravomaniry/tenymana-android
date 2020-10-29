package mg.maniry.tenymana.gameLogic.linkClear

import mg.maniry.tenymana.gameLogic.models.*
import mg.maniry.tenymana.gameLogic.shared.grid.*
import mg.maniry.tenymana.gameLogic.shared.words.resolveWith
import mg.maniry.tenymana.gameLogic.shared.words.resolved
import mg.maniry.tenymana.utils.Random
import mg.maniry.tenymana.utils.RandomImpl

class LinkClearPuzzle(
    initialGrid: Grid<CharAddress>,
    initialVerse: BibleVerse
) : Puzzle {
    private val random = RandomImpl() // maybe inject this?

    private val _grid = initialGrid.toCharGrid(initialVerse.words)
    private val hidden = initialGrid.calcHiddenWords(initialVerse.words)
    val grid: Grid<Character> get() = _grid

    private val words = initialVerse.words.toMutableList()
    override val verse = initialVerse.copy(words = words)

    private var _diff: List<Move>? = null
    val diff: List<Move>? get() = _diff

    private var _cleared: List<Point>? = null
    val cleared: List<Point>? get() = _cleared

    private var usedHelp = false
    override var completed = false
    override var score = 0

    override fun propose(move: Move): Boolean {
        reset()
        val selection = _grid.calcSelection(move)
        if (selection.isNotEmpty) {
            val indexes = words.resolveWith(selection.chars, hidden)
            if (indexes.isNotEmpty()) {
                updateResult(selection.points)
                incrementScore(indexes)
                return true
            }
        }
        return false
    }

    private fun reset() {
        completed = false
        _cleared = null
        _diff = null
    }

    private fun updateResult(points: List<Point>) {
        val diff0 = _grid.clear(points, gravity)
        completed = words.resolved
        if (!completed && _grid.firstVisibleMatch(words, visibleH, direction) == null) {
            usedHelp = true
            val match = _grid.createMatch(words, diff0, visibleH, direction, gravity, random)
            if (match == null) {
                completed = true
            } else {
                _diff = match.diff
                words[match.word] = words[match.word].resolvedVersion
                completed = words.resolved
                _cleared = points.toMutableList().apply { addAll(match.cleared) }.toSet().toList()
            }
        } else {
            _diff = diff0
            _cleared = points
        }
    }

    private fun incrementScore(resolved: List<Int>) {
        resolved.forEach {
            score += words[it].size
        }
        if (completed && !usedHelp) {
            score *= 2
        }
    }

    companion object {
        val direction = Point.directions
        val gravity = listOf(Point.DOWN, Point.LEFT)
        const val visibleH = 12
        const val width = 10

        fun build(verse: BibleVerse, random: Random): LinkClearPuzzle {
            val grid = buildLinkGrid(verse, random, width, visibleH)
            return LinkClearPuzzle(grid, verse)
        }
    }
}
