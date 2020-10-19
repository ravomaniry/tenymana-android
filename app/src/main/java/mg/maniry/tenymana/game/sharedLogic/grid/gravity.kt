package mg.maniry.tenymana.game.sharedLogic.grid

import mg.maniry.tenymana.game.models.Move
import mg.maniry.tenymana.game.puzzles.MutableGrid
import mg.maniry.tenymana.game.models.Point

fun <T> MutableGrid<T>.applyGravity(gravity: List<Point>): List<Move> {
    val rawMoves = mutableListOf<Move>()
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
                        rawMoves.add(Move.xy(x - d.x, y - d.y, x, y))
                        didUpdate = true
                    }
                }
            }
        }
    }
    return rawMoves.merge()
}

private fun List<Move>.merge(): List<Move> {
    val sources = hashMapOf<Point, Point>()
    forEach {
        val src = sources[it.a] ?: it.a
        sources.remove(it.a)
        sources[it.b] = src
    }
    val moves = mutableListOf<Move>()
    for (item in sources) {
        moves.add(Move(item.value, item.key))
    }
    return moves
}
