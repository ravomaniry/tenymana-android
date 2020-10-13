package mg.maniry.tenymana.game.sharedLogics.grid

import mg.maniry.tenymana.game.linkClear.gravity
import mg.maniry.tenymana.game.models.*
import mg.maniry.tenymana.game.models.Point.Companion.LEFT
import mg.maniry.tenymana.game.models.Point.Companion.RIGHT
import mg.maniry.tenymana.game.models.Point.Companion.UP

private data class Move(
    val src: Point,
    val dst: Point
)

fun MutableGrid<CharAddress>.placeWord(origin: Point, direction: Point, word: Word) {
    val done = applyHMoves(origin, direction, word)
    if (!done) {
        applyVMoves(origin, direction, word)
    }
    persist(word, origin, direction)
}

fun Grid<CharAddress>.testChange(
    origin: Point,
    direction: Point,
    word: Word
): MutableGrid<CharAddress> {
    return toMutable().apply {
        placeWord(origin, direction, word)
    }
}

private fun MutableGrid<CharAddress>.persist(word: Word, origin: Point, direction: Point) {
    for (i in 0 until word.size) {
        val p = origin + direction * i
        set(p.x, p.y, CharAddress(word.index, i))
    }
}

private fun MutableGrid<CharAddress>.applyHMoves(
    origin: Point,
    direction: Point,
    word: Word
): Boolean {
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

private fun MutableGrid<CharAddress>.applyVMoves(origin: Point, direction: Point, word: Word) {
    applyMoves(calcVMoves(origin, direction, word))
}

private fun Grid<CharAddress>.calcHMoves(origin: Point, direction: Point, word: Word): List<Move>? {
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

private fun Grid<CharAddress>.calcVMoves(origin: Point, direction: Point, word: Word): List<Move> {
    val moves = mutableListOf<Move>()
    val len = word.size
    moves.append(this, origin, len, direction, UP)
    return moves
}

private fun Grid<CharAddress>.countEmptyCellsAtRight(point: Point): Int {
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

private fun MutableGrid<CharAddress>.applyMoves(moves: List<Move>) {
    val cache = hashMapOf<Point, CharAddress?>()
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
    grid: Grid<CharAddress>,
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
    grid: Grid<CharAddress>,
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
