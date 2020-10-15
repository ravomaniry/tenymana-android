package mg.maniry.tenymana.game.models

import kotlin.math.absoluteValue
import kotlin.math.sign

data class Move(
    val a: Point,
    val b: Point
) {
    val direction: Point?

    init {
        val v = b - a
        direction = if (v.x == 0 || v.y == 0 || v.x.absoluteValue == v.y.absoluteValue)
            Point((b.x - a.x).sign, (b.y - a.y).sign)
        else null
    }

    override fun toString() = "$a->$b"

    companion object {
        fun xy(x0: Int, y0: Int, x1: Int, y1: Int): Move {
            return Move(Point(x0, y0), Point(x1, y1))
        }
    }
}

data class Point(
    val x: Int,
    val y: Int
) {
    operator fun plus(other: Point): Point {
        return Point(other.x + x, other.y + y)
    }

    operator fun times(n: Int): Point {
        return Point(x * n, y * n)
    }

    operator fun minus(other: Point): Point {
        return Point(x - other.x, y - other.y)
    }

    override fun toString() = "($x,$y)"

    companion object {
        val UP = Point(0, 1)
        val DOWN = Point(0, -1)
        val RIGHT = Point(1, 0)
        val LEFT = Point(-1, 0)
        val UP_LEFT = Point(-1, 1)
        val UP_RIGHT = Point(1, 1)
        val DOWN_LEFT = Point(-1, -1)
        val DOWN_RIGHT = Point(1, -1)

        val directions = listOf(UP, UP_RIGHT, RIGHT, DOWN_RIGHT, DOWN, DOWN_LEFT, LEFT, UP_LEFT)
    }
}
