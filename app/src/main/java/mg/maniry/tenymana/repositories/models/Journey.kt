package mg.maniry.tenymana.repositories.models

data class Journey(
    val id: String,
    val title: String,
    val description: String,
    val paths: List<Path>
)

data class Path(
    val id: String,
    val title: String,
    val description: String,
    val book: String,
    val chapter: Int,
    val start: Int,
    val end: Int
)
