package mg.maniry.tenymana.gameLogic.linkClear

import mg.maniry.tenymana.gameLogic.models.*
import mg.maniry.tenymana.gameLogic.shared.grid.*
import mg.maniry.tenymana.gameLogic.shared.words.resolveWith
import mg.maniry.tenymana.gameLogic.shared.words.resolved
import mg.maniry.tenymana.utils.Random
import mg.maniry.tenymana.utils.RandomImpl

interface LinkClearPuzzle : Puzzle {
    val grid: Grid<Character>
    val diff: List<Move>?
    val cleared: List<Point>?

    companion object {
        const val visibleH = 12
        const val width = 10
        val gravity = listOf(Point.DOWN, Point.LEFT)
        val directions = listOf(Point.UP, Point.RIGHT, Point.DOWN, Point.LEFT)

        fun build(verse: BibleVerse, random: Random): LinkClearPuzzle {
            val grid = buildLinkGrid(verse, random, width, visibleH)
            return LinkClearPuzzleImpl(grid, verse)
        }
    }
}

class LinkClearPuzzleImpl(
    initialGrid: Grid<CharAddress>,
    initialVerse: BibleVerse
) : LinkClearPuzzle {
    private val visibleH = LinkClearPuzzle.visibleH
    private val gravity = LinkClearPuzzle.gravity
    private val directions = LinkClearPuzzle.directions

    private val random = RandomImpl()

    private val hidden = initialGrid.calcHiddenWords(initialVerse.words)
    override val grid: MutableGrid<Character> = initialGrid.toCharGrid(initialVerse.words)

    private val words = initialVerse.words.toMutableList()
    override val verse = initialVerse.copy(words = words)
    override var diff: List<Move>? = null
    override var cleared: List<Point>? = null

    private var usedHelp = false
    override var completed = false
    override var score = 0

    override fun propose(move: Move): Boolean {
        reset()
        val selection = grid.calcSelection(move)
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
        cleared = null
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
                cleared = points.toMutableList().apply { addAll(match.cleared) }.toSet().toList()
            }
        } else {
            diff = diff0
            cleared = points
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
}
