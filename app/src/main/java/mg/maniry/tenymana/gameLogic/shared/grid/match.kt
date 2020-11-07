package mg.maniry.tenymana.gameLogic.shared.grid

import mg.maniry.tenymana.gameLogic.models.*
import mg.maniry.tenymana.utils.InsideOutIterator
import mg.maniry.tenymana.utils.Random
import kotlin.math.max
import kotlin.math.min

fun Grid<Character>.firstVisibleMatch(
    words: List<Word>,
    visibleH: Int,
    directions: List<Point>
): Move? {
    val wInsCope = words.filter { !it.resolved }
    for (y in 0 until max(visibleH, h)) {
        for (x in 0 until w) {
            val c = get(x, y)
            if (c != null) {
                for (word in wInsCope) {
                    if (word.startsWith(c)) {
                        val points = findValidMove(word, Point(x, y), directions, visibleH)
                        if (points != null && points.size > 1) {
                            return Move(points.first(), points.last())
                        }
                    }
                }
            }
        }
    }
    return null
}

fun Grid<Character>.randomMatch(
    words: List<Word>,
    visibleH: Int,
    directions: List<Point>,
    random: Random
): List<Point>? {
    val wordsInScope = words.filter { !it.resolved }
    val max = w * min(visibleH, h) - 1
    val it = InsideOutIterator((0..max).toList(), random.int(0, max))
    while (it.hasNext) {
        val point = it.next().toPoint(w)
        val c = get(point)
        if (c != null) {
            for (word in wordsInScope) {
                if (word.startsWith(c)) {
                    val points = findValidMove(word, point, directions, visibleH)
                    if (points != null) {
                        return points
                    }
                }
            }
        }
    }
    return null
}

private fun Grid<Character>.findValidMove(
    word: Word,
    origin: Point,
    directions: List<Point>,
    visibleH: Int
): List<Point>? {
    for (d in directions) {
        val a = Point(origin.x, origin.y)
        val b = a + d * (word.size - 1)
        if (b.y < visibleH) {
            val selection = calcSelection(Move(a, b), directions)
            if (selection.chars.isNotEmpty() && word.sameChars(selection.chars)) {
                return selection.points
            }
        }
    }
    return null
}

private fun Int.toPoint(width: Int): Point {
    return Point(this % width, this / width)
}
