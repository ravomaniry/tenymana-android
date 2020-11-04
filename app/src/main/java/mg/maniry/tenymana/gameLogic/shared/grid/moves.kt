package mg.maniry.tenymana.gameLogic.shared.grid

import mg.maniry.tenymana.gameLogic.models.Grid
import mg.maniry.tenymana.gameLogic.models.Move
import mg.maniry.tenymana.gameLogic.models.Point

private const val XY_RATIO_W = 1.0
private const val

fun Grid<*>.calcOrigins(visibleH: Int): MutableList<Point> {
    val starts = mutableListOf<Point>()
    forEachUntilY(visibleH) { x, y, p ->
        if (y == 0 || p != null || get(x, y - 1) != null) {
            starts.add(Point(x, y))
        }
    }
    return starts
}

fun Grid<*>.calcAllMoves(visibleH: Int, len: Int): List<Pair<Move, Float>> {
    val moves = mutableListOf<Pair<Move, Float>>()
    val origins = calcOrigins(visibleH)

    return moves
}

private fun Grid<*>.calcXYRatioScore(old: Grid<*>): Double {
    return XY_RATIO_W * (xyRatio - old.xyRatio)
}

private val Grid<*>.xyRatio: Double
    get() {
        var totalX = 0
        var totalY = 0.0
        forEach { x, y, v ->
            if (v != null) {
                totalX += x
                totalY += y
            }
        }
        return totalX / totalY
    }
