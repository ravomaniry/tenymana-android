package mg.maniry.tenymana.game.sharedLogics

import mg.maniry.tenymana.game.models.Grid
import mg.maniry.tenymana.game.models.MutableGrid
import mg.maniry.tenymana.game.models.Point
import mg.maniry.tenymana.game.models.Word

fun MutableGrid.applyGravity(gravity: List<Point>) {
    for (d in gravity) {
        var didUpdate = true
        while (didUpdate) {
            didUpdate = false
            forEach { x, y, p ->
                if (p == null) {
                    val neighbor = this[x - d.x, y - d.y]
                    if (neighbor != null) {
                        set(x - d.x, y - d.y, null)
                        set(x, y, neighbor)
                        didUpdate = true
                    }
                }
            }
        }
    }
}

fun MutableGrid.persist(origin: Point, direction: Point, word: Word) {
    for (i in 0 until word.size) {
        val p = origin + direction * i
        set(p.x, p.y, Point(word.index, i))
    }
}

fun Grid.testChange(origin: Point, direction: Point, word: Word): MutableGrid {
    return toMutable().apply {
        persist(origin, direction, word)
    }
}
