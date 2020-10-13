package mg.maniry.tenymana.game.sharedLogics.grid

import mg.maniry.tenymana.game.models.MutableGrid
import mg.maniry.tenymana.game.models.Point

fun <T> MutableGrid<T>.applyGravity(gravity: List<Point>) {
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
