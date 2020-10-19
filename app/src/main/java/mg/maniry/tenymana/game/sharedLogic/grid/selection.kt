package mg.maniry.tenymana.game.sharedLogic.grid

import mg.maniry.tenymana.game.models.Character
import mg.maniry.tenymana.game.puzzles.Grid
import mg.maniry.tenymana.game.models.Move
import mg.maniry.tenymana.game.models.Point

data class Selection(
    val points: List<Point> = listOf(),
    val chars: List<Character> = listOf()
) {
    val isNotEmpty: Boolean get() = chars.isNotEmpty()
}

fun Grid<Character>.calcSelection(move: Move): Selection {
    if (move.direction != null && move.direction in Point.directions) {
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
