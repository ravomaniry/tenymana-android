package mg.maniry.tenymana.repositories.models

data class Journey(
    val id: String,
    val title: String = "",
    val description: String = "",
    val paths: List<Path> = emptyList()
) {
    val size: Int
        get() {
            var s = 0
            paths.forEach { s += 1 + it.end - it.start }
            return s
        }

    override fun toString(): String {
        return "id=$id|title=$title|description=$description|" +
                "paths=${paths.joinToString { "$it\n" }}"
    }
}

data class Path(
    val name: String = "",
    val description: String = "",
    val book: String,
    val chapter: Int,
    val start: Int,
    val end: Int
) {
    val size: Int get() = end - start + 1
    override fun toString(): String {
        return "name=$name|description=$description|($book $chapter:$start-$end)"
    }
}
