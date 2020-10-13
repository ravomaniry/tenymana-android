package mg.maniry.tenymana.game.models

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
