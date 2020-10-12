package mg.maniry.tenymana.game.models

private typealias ForEachCb = (x: Int, y: Int, p: Point?) -> Unit

open class Grid(
    private val cells: List<List<Point?>>
) {
    val h: Int get() = cells.size
    val w: Int get() = if (cells.isEmpty()) 0 else cells[0].size

    fun forEach(cb: ForEachCb) {
        cells.forEachIndexed { y, row ->
            row.forEachIndexed { x, p ->
                cb(x, y, p)
            }
        }
    }

    operator fun get(x: Int, y: Int): Point? {
        return if (y >= 0 && y < cells.size && x >= 0 && x < cells[y].size)
            cells[y][x]
        else null
    }

    operator fun get(point: Point) = get(point.x, point.y)

    fun toMutable(): MutableGrid {
        return MutableGrid(
            w,
            cells.map { it.toMutableList() }.toMutableList()
        )
    }

    fun canContain(p: Point): Boolean {
        return p.x >= 0 && p.y >= 0 && p.x < w
    }

    override fun hashCode() = cells.hashCode()
    override fun equals(other: Any?) = other is Grid && other.cells == cells
    override fun toString() = cells.joinToString("\n") { it.toString() }
}

data class MutableGrid(
    private val width: Int,
    private val cells: MutableList<MutableList<Point?>> = mutableListOf()
) : Grid(cells) {
    fun toGrid(): Grid {
        return Grid(
            cells.map { it.toList() }
        )
    }

    fun set(x: Int, y: Int, value: Point?) {
        addMissingRows(y)
        cells[y][x] = value
    }

    private fun addMissingRows(y: Int) {
        // always add an empty row at the top
        while (cells.size <= y + 1) {
            cells.add(MutableList(width) { null })
        }
    }

    override fun toString() = super.toString()

    init {
        if (cells.isEmpty()) {
            set(0, 0, null)
        }
    }
}
