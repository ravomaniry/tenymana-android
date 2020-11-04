package mg.maniry.tenymana.gameLogic.shared.grid

import mg.maniry.tenymana.gameLogic.models.*
import mg.maniry.tenymana.utils.Random
import kotlin.math.min

private const val XY_RATIO_W = 2.0
private const val DIFF_LEN_W = 1.0

data class MoveWithScore(
    val move: Move,
    val score: Double
)

fun List<MoveWithScore>.getRandomByRate(random: Random): Move {
    var score0 = 0.0
    forEach {
        score0 = min(it.score, score0)
    }
    score0--
    var move = get(0).move
    var maxScore = get(0).score
    forEach {
        val score = (it.score - score0) * random.double()
        if (score > maxScore) {
            maxScore = score
            move = it.move
        }
    }
    return move
}

fun Grid<CharAddress>.calcScoredMoves(
    visibleH: Int,
    word: Word,
    directions: List<Point>,
    gravity: List<Point>
): List<MoveWithScore> {
    val wordLen = word.size
    val moves = mutableListOf<MoveWithScore>()
    val origins = calcOrigins(visibleH)
    for (origin in origins) {
        for (dir in directions) {
            val move = Move(origin, dir)
            if (canContainMove(move, wordLen, visibleH)) {
                val next = testChange(origin, dir, word, gravity)
                val withG = next.toMutable().apply { applyGravity(gravity) }
                if (next == withG) {
                    val xyRatioScore = next.calcXYRatioScore(this)
                    val diffsScore = next.calcDiffScore(this, wordLen)
                    moves.add(MoveWithScore(move, xyRatioScore + diffsScore))
                }
            }
        }
    }
    return moves
}

private fun Grid<*>.calcOrigins(visibleH: Int): MutableList<Point> {
    val starts = mutableListOf<Point>()
    forEachUntilY(visibleH) { x, y, p ->
        if (y == 0 || p != null || get(x, y - 1) != null) {
            starts.add(Point(x, y))
        }
    }
    return starts
}

private fun Grid<CharAddress>.canContainMove(move: Move, len: Int, visibleH: Int): Boolean {
    val end = move.a + move.b * len
    if (end.y >= visibleH) {
        return false
    }
    for (di in 0 until len) {
        val p = move.a + (move.b * di)
        if (!canContain(p)) {
            return false
        }
    }
    return true
}

private fun Grid<*>.calcDiffScore(old: Grid<*>, wordLen: Int): Double {
    var count = 0
    forEach { x, y, v ->
        val oldV = old[x, y]
        if (v != null && oldV != null && v != oldV) {
            count++
        }
    }
    return DIFF_LEN_W * count / wordLen
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
        if (totalY == 0.0) {
            totalY = 1.0
        }
        return totalX / totalY
    }
