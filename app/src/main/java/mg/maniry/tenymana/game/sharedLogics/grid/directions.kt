package mg.maniry.tenymana.game.sharedLogics.grid

import mg.maniry.tenymana.game.linkClear.gravity
import mg.maniry.tenymana.game.models.CharAddress
import mg.maniry.tenymana.game.models.Grid
import mg.maniry.tenymana.game.models.Point
import mg.maniry.tenymana.game.models.Word

fun Grid<CharAddress>.calcDirections(
    directions: List<Point>,
    origin: Point,
    word: Word,
    visibleH: Int
): List<Point> {
    val dirs = mutableListOf<Point>()
    for (dir in directions) {
        if (directionIsPossible(origin, dir, word, visibleH)) {
            dirs.add(dir)
        }
    }
    return dirs
}

private fun Grid<CharAddress>.directionIsPossible(
    origin: Point,
    dir: Point,
    word: Word,
    visibleH: Int
): Boolean {
    val len = word.size
    val end = origin + dir * len
    if (end.y >= visibleH) {
        return false
    }
    for (di in 0 until len) {
        val p = origin + (dir * di)
        if (!canContain(p) || this[p] != null) {
            return false
        }
    }
    val test = testChange(origin, dir, word)
    val withG = test.toMutable().apply { applyGravity(gravity) }
    return test == withG
}
