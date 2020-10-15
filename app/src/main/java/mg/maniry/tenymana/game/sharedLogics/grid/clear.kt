package mg.maniry.tenymana.game.sharedLogics.grid

import mg.maniry.tenymana.game.models.Move
import mg.maniry.tenymana.game.models.MutableGrid
import mg.maniry.tenymana.game.models.Point

fun MutableGrid<*>.clear(points: List<Point>, gravity: List<Point>): List<Move> {
    points.forEach {
        set(it.x, it.y, null)
    }
    return applyGravity(gravity)
}
