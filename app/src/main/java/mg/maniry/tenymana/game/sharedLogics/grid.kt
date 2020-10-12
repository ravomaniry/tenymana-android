package mg.maniry.tenymana.game.sharedLogics

import mg.maniry.tenymana.game.linkClear.gravity
import mg.maniry.tenymana.game.models.*

private data class Move(
    val src: Point,
    val dst: Point
)

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
    val done = applyHMoves(origin, direction, word)
    if (!done) {
        applyVMoves(origin, direction, word)
    }
    for (i in 0 until word.size) {
        val p = origin + direction * i
        set(p.x, p.y, Point(word.index, i))
    }
}

private fun MutableGrid.applyHMoves(origin: Point, direction: Point, word: Word): Boolean {
    val hMoves = calcHMoves(origin, direction, word)
    if (hMoves != null) {
        val copy = toMutable().apply {
            applyMoves(hMoves)
            applyGravity(gravity)
        }
        if (copy == this) {
            applyMoves(hMoves)
            return true
        }
    }
    return false
}

private fun MutableGrid.applyVMoves(origin: Point, direction: Point, word: Word) {
    applyMoves(calcVMoves(origin, direction, word))
}

fun Grid.testChange(origin: Point, direction: Point, word: Word): MutableGrid {
    return toMutable().apply {
        persist(origin, direction, word)
    }
}

private fun Grid.calcHMoves(origin: Point, direction: Point, word: Word): List<Move>? {
    val moves = mutableListOf<Move>()
    val len = word.size
    if (direction == RIGHT || direction == LEFT) {
        val p = if (direction == RIGHT) origin + direction * (len - 1) else origin
        if (countEmptyCellsAtRight(p) < len) {
            return null
        }
        moves.appendH(this, p, len, direction)
        return moves
    } else {
        for (i in 0 until len) {
            val p = origin + direction * i
            if (countEmptyCellsAtRight(p) == 0) {
                return null
            }
            moves.appendH(this, p, 1, direction)
        }
    }
    return moves
}

private fun Grid.calcVMoves(origin: Point, direction: Point, word: Word): List<Move> {
    val moves = mutableListOf<Move>()
    val len = word.size
    val p = if (direction == UP) origin + direction * (len - 1) else origin
    moves.appendV(this, p, len, direction)
    return moves
}

private fun Grid.countEmptyCellsAtRight(point: Point): Int {
    var c = 0
    if (point.y >= h) {
        return w
    }
    for (x in (point.x + 1) until w) {
        if (get(x, point.y) == null) {
            c++
        }
    }
    return c
}

private fun MutableGrid.applyMoves(moves: List<Move>) {
    val cache = hashMapOf<String, Point?>()
    for (m in moves) {
        cache[m.src.toString()] = get(m.src)
        set(m.src.x, m.src.y, null)
    }
    for (m in moves) {
        set(m.dst.x, m.dst.y, cache[m.src.toString()])
    }
}

private fun MutableList<Move>.appendH(
    grid: Grid,
    point: Point,
    len: Int,
    direction: Point
) {
    if (direction == LEFT || direction == RIGHT) {
        for (i in 0 until len) {
            val src = point + RIGHT * i
            if (grid[src] != null) {
                add(Move(src, point + RIGHT * (i + len)))
            }
        }
    } else {
        for (dx in 0 until len) {
            for (dy in 0 until grid.h) {
                val src = Point(point.x + dx, point.y + dy)
                if (grid[src] != null) {
                    add(Move(src, src + UP))
                }
            }
        }
    }
}

private fun MutableList<Move>.appendV(grid: Grid, point: Point, len: Int, direction: Point) {
    if (direction == UP || direction == DOWN) {
        for (i in 0 until len) {
            val src = point + UP * i
            if (grid[src] != null) {
                add(Move(src, point + UP * (i + len)))
            }
        }
    } else {
        for (dx in 0 until len) {
            for (dy in 0 until grid.h) {
                val src = Point(point.x + dx, point.y + dy)
                if (grid[src] != null) {
                    add(Move(src, src + UP))
                }
            }
        }
    }
}
