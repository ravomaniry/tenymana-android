package mg.maniry.tenymana.game.linkClear

import androidx.lifecycle.MutableLiveData
import mg.maniry.tenymana.game.models.*
import mg.maniry.tenymana.game.sharedLogic.grid.*
import mg.maniry.tenymana.game.sharedLogic.words.resolveWith
import mg.maniry.tenymana.game.sharedLogic.words.resolved
import mg.maniry.tenymana.utils.RandomImpl

class LinkClearBoard(
    initialGrid: Grid<CharAddress>,
    initialVerse: BibleVerse
) : Board {
    private val random = RandomImpl() // maybe inject this?

    private val _grid = initialGrid.toCharGrid(initialVerse.words)
    private val hidden = initialGrid.calcHiddenWords(initialVerse.words)
    val grid: Grid<Character> get() = _grid

    private val words = initialVerse.words.toMutableList()
    val verse = initialVerse.copy(words = words)

    private var _diff: List<Move>? = null
    val diff: List<Move>? get() = _diff

    private var _cleared: List<Point>? = null
    val cleared: List<Point>? get() = _cleared

    private var usedHelp = false
    private var _completed = false
    override val completed = MutableLiveData(false)

    private var _score = 0
    override val score = MutableLiveData(0)

    override fun propose(move: Move): Boolean {
        reset()
        val selection = _grid.calcSelection(move)
        if (selection.isNotEmpty) {
            val indexes = words.resolveWith(selection.chars, hidden)
            if (indexes.isNotEmpty()) {
                updateResult(selection.points)
                incrementScore(indexes)
                syncLiveData()
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

    private fun updateResult(points: List<Point>) {
        val diff0 = _grid.clear(points, gravity)
        _completed = words.resolved
        if (!_completed && _grid.firstVisibleMatch(words, visibleH, direction) == null) {
            usedHelp = true
            val match = _grid.createMatch(words, diff0, visibleH, direction, gravity, random)
            if (match == null) {
                _completed = true
            } else {
                _diff = match.diff
                words[match.word] = words[match.word].resolvedVersion
                _completed = words.resolved
                _cleared = points.toMutableList().apply { addAll(match.cleared) }.toSet().toList()
            }
        } else {
            _diff = diff0
            _cleared = points
        }
    }

    private fun incrementScore(resolved: List<Int>) {
        resolved.forEach {
            _score += words[it].size
        }
        if (_completed && !usedHelp) {
            _score *= 2
        }
    }

    private fun syncLiveData() {
        if (score.value != _score) {
            score.postValue(_score)
        }
        if (completed.value != _completed) {
            completed.postValue(_completed)
        }
    }

    companion object {
        val direction = Point.directions
        val gravity = listOf(Point.DOWN, Point.LEFT)
        const val visibleH = 12
    }
}
