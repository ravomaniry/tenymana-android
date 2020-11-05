package mg.maniry.tenymana.utils

class Memo<T>(
    private val depGetter: () -> List<Any?>,
    private val valueGetter: () -> T
) {
    private var prevDep: List<Any?>? = null
    private var cache: T? = null

    val value: T
        get() {
            val dep = depGetter()
            if (dep != prevDep) {
                prevDep = dep
                cache = valueGetter()
                prevDep = dep
                return cache!!
            }
            return cache!!
        }
}
