package mg.maniry.tenymana.gameLogic.shared.grid

import mg.maniry.tenymana.gameLogic.models.Character
import mg.maniry.tenymana.gameLogic.models.Grid
import mg.maniry.tenymana.gameLogic.models.Move
import mg.maniry.tenymana.gameLogic.models.Point

data class Selection(
    val points: List<Point> = listOf(),
    val chars: List<Character> = listOf()
) {
    val isNotEmpty: Boolean get() = chars.isNotEmpty()
}

fun Grid<Character>.calcSelection(move: Move, directions: List<Point>): Selection {
    if (move.direction != null && move.direction in directions) {
        val chars = mutableListOf<Character>()
        val points = mutableListOf<Point>()
        var point = move.a
        var next = true
        while (next) {
            val c = get(point)
            if (c != null) {
                points.add(point)
                chars.add(c)
            }
            next = point != move.b
            point += move.direction
        }
        return Selection(points, chars)
    }
    return Selection()
}
