package mg.maniry.tenymana.gameLogic.shared.grid

import mg.maniry.tenymana.gameLogic.models.*
import mg.maniry.tenymana.gameLogic.models.Grid
import kotlin.math.max

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
                        val move = findValidMove(word, Point(x, y), directions, visibleH)
                        if (move != null) {
                            return move
                        }
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
): Move? {
    for (d in directions) {
        val a = Point(origin.x, origin.y)
        val b = a + d * (word.size - 1)
        if (b.y < visibleH) {
            val selection = calcSelection(Move(a, b))
            if (selection.chars.isNotEmpty() && word.sameChars(selection.chars)) {
                return Move(a, b)
            }
        }
    }
    return null
}
