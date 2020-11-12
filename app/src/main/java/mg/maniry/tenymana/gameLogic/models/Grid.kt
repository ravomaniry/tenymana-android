package mg.maniry.tenymana.gameLogic.models

private typealias ForEachCb<T> = (x: Int, y: Int, p: T?) -> Unit

open class Grid<T>(
    val cells: List<List<T?>>
) {
    val h: Int get() = cells.size
    val w: Int get() = if (cells.isEmpty()) 0 else cells[0].size

    fun forEach(cb: ForEachCb<T>) {
        cells.forEachIndexed { y, row ->
            row.forEachIndexed { x, v -> cb(x, y, v) }
        }
    }

    fun forEachUntilY(yM: Int, cb: ForEachCb<T>) {
        cells.forEachIndexed { y, row ->
            if (y < yM) {
                row.forEachIndexed { x, v -> cb(x, y, v) }
            }
        }
    }

    operator fun get(x: Int, y: Int): T? {
        return if (y >= 0 && y < cells.size && x >= 0 && x < cells[y].size)
            cells[y][x]
        else null
    }

    operator fun get(point: Point) = get(point.x, point.y)

    fun toGrid(): Grid<T> {
        return Grid(
            cells.map { it.toList() }
        )
    }

    fun toMutable(): MutableGrid<T> {
        return MutableGrid(
            w,
            cells.map { it.toMutableList() }.toMutableList()
        )
    }

    fun canContain(p: Point): Boolean {
        return p.x >= 0 && p.y >= 0 && p.x < w
    }

    override fun hashCode() = cells.hashCode()
    override fun equals(other: Any?) = other is Grid<*> && other.cells == cells
    override fun toString() = cells.joinToString("\n") { it.toString() }
}

data class MutableGrid<T>(
    private val width: Int,
    private val mutableCells: MutableList<MutableList<T?>> = mutableListOf()
) : Grid<T>(mutableCells) {
    fun set(x: Int, y: Int, value: T?) {
        addMissingRows(y)
        mutableCells[y][x] = value
    }

    private fun addMissingRows(y: Int) {
        // always add an empty row at the top
        while (mutableCells.size <= y + 1) {
            mutableCells.add(MutableList(width) { null })
        }
    }

    override fun toString() = super.toString()

    init {
        if (mutableCells.isEmpty()) {
            set(0, 0, null)
        }
    }
}
