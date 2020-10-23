package mg.maniry.tenymana.repositories.models

data class Journey(
    val id: String,
    val title: String,
    val description: String,
    val paths: List<Path>
) {
    val size: Int
        get() {
            var s = 0
            paths.forEach { s += 1 + it.end - it.start }
            return s
        }

    companion object {
        fun empty(id: String) = Journey(id, "", "", emptyList())
    }
}

data class Path(
    val name: String,
    val description: String,
    val book: String,
    val chapter: Int,
    val start: Int,
    val end: Int
)
