package mg.maniry.tenymana.utils

fun <T> List<T>.findIndex(criteria: (e: T) -> Boolean): Int {
    for (i in 0 until size) {
        if (criteria(this[i])) {
            return i
        }
    }
    return -1
}
