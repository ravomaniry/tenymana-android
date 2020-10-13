package mg.maniry.tenymana.game.models

private typealias ForEachCb<T> = (x: Int, y: Int, p: T?) -> Unit
private typealias MapCb<A, B> = (x: Int, y: Int, p: A?) -> B?

open class Grid<T>(
    private val cells: List<List<T?>>
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

    fun toMutable(): MutableGrid<T> {
        return MutableGrid(
            w,
            cells.map { it.toMutableList() }.toMutableList()
        )
    }

    fun canContain(p: Point): Boolean {
        return p.x >= 0 && p.y >= 0 && p.x < w
    }

    fun <X> map(cb: MapCb<T, X>): Grid<X> {
        return Grid(
            cells.mapIndexed { y, row ->
                row.mapIndexed { x, v -> cb(x, y, v) }
            }
        )
    }

    override fun hashCode() = cells.hashCode()
    override fun equals(other: Any?) = other is Grid<*> && other.cells == cells
    override fun toString() = cells.joinToString("\n") { it.toString() }
}

data class MutableGrid<T>(
    private val width: Int,
    private val cells: MutableList<MutableList<T?>> = mutableListOf()
) : Grid<T>(cells) {
    fun toGrid(): Grid<T> {
        return Grid(
            cells.map { it.toList() }
        )
    }

    fun set(x: Int, y: Int, value: T?) {
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
