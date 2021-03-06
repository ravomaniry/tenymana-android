package mg.maniry.tenymana.utils

class InsideOutIterator<T>(
    private val list: List<T>,
    private val i0: Int
) {

    private val indexes = mutableListOf(i0)
    private var i = -1

    val hasNext: Boolean get() = i < list.size - 1

    fun next(): T {
        i++
        return list[indexes[i]]
    }

    private fun buildIndexes() {
        var minI = i0
        var maxI = i0
        val size = list.size
        while (minI > 0 || maxI < size) {
            minI--
            maxI++
            indexes.add((size + minI) % size)
            indexes.add((size + maxI) % size)
        }
    }

    init {
        buildIndexes()
    }

    companion object {
        fun <T> random(list: List<T>, rand: Random): InsideOutIterator<T> {
            val i0 = rand.int(0, list.size - 1)
            return InsideOutIterator(list, i0)
        }
    }
}
