package mg.maniry.tenymana.gameLogic.shared.grid

import mg.maniry.tenymana.gameLogic.models.Grid
import mg.maniry.tenymana.gameLogic.models.Point

fun Grid<*>.calcOrigins(visibleH: Int): MutableList<Point> {
    val starts = mutableListOf<Point>()
    forEachUntilY(visibleH) { x, y, p ->
        if (y == 0 || p != null || get(x, y - 1) != null) {
            starts.add(Point(x, y))
        }
    }
    return starts
}
