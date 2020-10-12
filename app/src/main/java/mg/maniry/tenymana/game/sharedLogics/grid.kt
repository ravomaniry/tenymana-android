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

fun MutableGrid.placeWord(origin: Point, direction: Point, word: Word) {
    val done = applyHMoves(origin, direction, word)
    if (!done) {
        applyVMoves(origin, direction, word)
    }
    persist(word, origin, direction)
}

private fun MutableGrid.persist(word: Word, origin: Point, direction: Point) {
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
        placeWord(origin, direction, word)
    }
}

private fun Grid.calcHMoves(origin: Point, direction: Point, word: Word): List<Move>? {
    val moves = mutableListOf<Move>()
    val len = word.size
    val leftP = if (direction == RIGHT) origin + direction * (len - 1) else origin
    if ((direction == RIGHT || direction == LEFT)) {
        if (countEmptyCellsAtRight(leftP) < len) {
            return null
        }
    } else {
        for (i in 0 until len) {
            if (countEmptyCellsAtRight(origin + direction * i) == 0) {
                return null
            }
        }
    }
    moves.append(this, origin, len, direction, RIGHT)
    return moves
}

private fun Grid.calcVMoves(origin: Point, direction: Point, word: Word): List<Move> {
    val moves = mutableListOf<Move>()
    val len = word.size
    moves.append(this, origin, len, direction, UP)
    return moves
}

private fun Grid.countEmptyCellsAtRight(point: Point): Int {
    var c = 0
    if (point.y >= h) {
        return w
    }
    for (x in point.x until w) {
        if (get(x, point.y) == null) {
            c++
        }
    }
    return c
}

private fun MutableGrid.applyMoves(moves: List<Move>) {
    val cache = hashMapOf<Point, Point?>()
    val dstCache = hashMapOf<Point, Boolean>()
    for (m in moves) {
        cache[m.src] = get(m.src)
        dstCache[m.dst] = true
    }
    for (m in moves) {
        set(m.dst.x, m.dst.y, cache[m.src])
        if (dstCache[m.src] != true) {
            set(m.src.x, m.src.y, null)
        }
    }
}

private fun MutableList<Move>.append(
    grid: Grid,
    origin: Point,
    len: Int,
    direction: Point,
    slideDir: Point
) {
    if (direction == slideDir || direction == slideDir * -1) {
        addItems(grid, origin, direction, len, slideDir, len)
    } else {
        addItems(grid, origin, direction, len, slideDir, 1)
    }
}

private fun MutableList<Move>.addItems(
    grid: Grid,
    origin: Point,
    direction: Point,
    len: Int,
    slideDir: Point,
    slideLen: Int
) {
    for (i in 0 until len) {
        val src = origin + direction * i
        val dst = src + slideDir * slideLen
        if (grid[src] != null && grid.canContain(dst)) {
            add(Move(src, dst))
        }
    }
}
