package mg.maniry.tenymana.game.sharedLogic.grid

import mg.maniry.tenymana.game.models.*
import mg.maniry.tenymana.utils.InsideOutIterator
import mg.maniry.tenymana.utils.Random
import mg.maniry.tenymana.utils.findIndex
import kotlin.math.max
import kotlin.math.min

fun MutableGrid<Character>.createMatch(
    words: MutableList<Word>,
    diff: MutableList<Move>,
    visibleH: Int,
    directions: List<Point>,
    gravity: List<Point>,
    random: Random
): Int? {
    val wInScope = unresolvedWords(words)
    val ns = (0 until w * min(visibleH, h)).toList()
    val it = InsideOutIterator.random(ns, random)
    while (it.hasNext) {
        val origin = nthPoint(it.next())
        val c = get(origin)
        if (c != null) {
            for (word in wInScope) {
                if (word.startsWith(c)) {
                    val points = findNearestMatch(word, origin)
                    if (points != null) {
                        val copy = toMutable().apply { clear(points, gravity) }
                        if (copy.firstVisibleMatch(words, visibleH, directions) != null) {
                            val newDiff = clear(points, gravity)
                            diff.updateWith(newDiff, points)
                            return word.index
                        }
                    }
                }
            }
        }
    }
    return null
}

private fun Grid<*>.nthPoint(n: Int): Point {
    return Point(n % w, n / w)
}

private fun Grid<Character>.findNearestMatch(word: Word, origin: Point): List<Point>? {
    var xL = origin.x - 1
    var xR = origin.x
    var yUp = origin.y
    var yDown = origin.y - 1
    var cI = 1
    val points = mutableListOf(origin)
    val seen = mutableSetOf(origin)
    while (xL >= 0 || xR < w || yDown >= 0 || yUp < h) {
        for (x in max(0, xL)..min(w - 1, xR)) {
            for (y in max(0, yDown)..min(h - 1, yUp)) {
                val p = Point(x, y)
                if (!seen.contains(p)) {
                    seen.add(p)
                    if (get(x, y)?.isSameAs(word[cI]) == true) {
                        cI++
                        points.add(p)
                        if (cI == word.size) {
                            return points
                        }
                    }
                }
            }
        }
        yUp++
        yDown--
        xL--
        xR++
    }
    return null
}

private fun unresolvedWords(words: List<Word>): List<Word> {
    return words.filter { !it.resolved }
}

private fun MutableList<Move>.updateWith(newDiff: List<Move>, cleared: List<Point>) {
    removeClearedDest(cleared)
    updateDest(newDiff)
}

private fun MutableList<Move>.removeClearedDest(cleared: List<Point>) {
    var i = 0
    while (i < this.size - 1) {
        val move = this[i]
        if (move.b in cleared) {
            removeAt(i)
        } else {
            i++
        }
    }
}

private fun MutableList<Move>.updateDest(newDiff: List<Move>) {
    val remainingDiff = newDiff.toMutableList()
    forEachIndexed { index, move ->
        val newI = remainingDiff.findIndex { it.a == move.b }
        if (newI >= 0) {
            this[index] = move.copy(b = remainingDiff[newI].b)
            remainingDiff.removeAt(newI)
        }
    }
    addAll(remainingDiff)
}
