package mg.maniry.tenymana.gameLogic.shared.grid

import mg.maniry.tenymana.gameLogic.models.Move
import mg.maniry.tenymana.gameLogic.models.MutableGrid
import mg.maniry.tenymana.gameLogic.models.Point

fun MutableGrid<*>.clear(points: List<Point>, gravity: List<Point>): List<Move> {
    points.forEach {
        set(it.x, it.y, null)
    }
    return applyGravity(gravity)
}
