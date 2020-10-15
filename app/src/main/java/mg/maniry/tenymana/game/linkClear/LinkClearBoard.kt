package mg.maniry.tenymana.game.linkClear

import mg.maniry.tenymana.game.models.*
import mg.maniry.tenymana.game.sharedLogics.grid.calcHiddenWords
import mg.maniry.tenymana.game.sharedLogics.grid.calcSelection
import mg.maniry.tenymana.game.sharedLogics.grid.clear
import mg.maniry.tenymana.game.sharedLogics.grid.toCharGrid
import mg.maniry.tenymana.game.sharedLogics.words.resolved
import mg.maniry.tenymana.game.sharedLogics.words.resolveWith

class LinkClearBoard(
    initialGrid: Grid<CharAddress>,
    initialVerse: BibleVerse
) {
    private val _grid = initialGrid.toCharGrid(initialVerse.words)
    private val hidden = initialGrid.calcHiddenWords(initialVerse.words)
    val grid: Grid<Character> get() = _grid

    private val words = initialVerse.words.toMutableList()
    val verse = initialVerse.copy(words = words)

    private var _diff: List<Move>? = null
    val diff: List<Move>? get() = _diff

    private var _cleared: List<Point>? = null
    val cleared: List<Point>? get() = _cleared

    private var _completed = false
    val completed: Boolean get() = _completed

    fun propose(move: Move): Boolean {
        reset()
        val selection = _grid.calcSelection(move)
        if (selection.isNotEmpty) {
            val indexes = words.resolveWith(selection.chars, hidden)
            if (indexes.isNotEmpty()) {
                _diff = _grid.clear(selection.points, gravity)
                _cleared = selection.points
                _completed = words.resolved
                return true
            }
        }
        return false
    }

    private fun reset() {
        _completed = false
        _cleared = null
        _diff = null
    }
}
