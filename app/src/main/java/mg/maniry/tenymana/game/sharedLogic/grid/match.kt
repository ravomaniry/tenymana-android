package mg.maniry.tenymana.game.sharedLogic.grid

import mg.maniry.tenymana.game.models.*
import kotlin.math.max

fun Grid<Character>.firstVisibleMatch(
    words: List<Word>,
    gridH: Int,
    directions: List<Point>
): Move? {
    val wInsCope = words.filter { !it.resolved }
    for (y in 0 until max(gridH, h)) {
        for (x in 0 until w) {
            val c = get(x, y)
            if (c != null) {
                for (w in wInsCope) {
                    if (w.startsWith(c)) {
                        for (d in directions) {
                            val a = Point(x, y)
                            val b = a + d * (w.size - 1)
                            if (b.y < gridH) {
                                val selection = calcSelection(Move(a, b))
                                if (selection.chars.isNotEmpty() && w.sameChars(selection.chars)) {
                                    return Move(a, b)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    return null
}
