package mg.maniry.tenymana.repositories.models

data class BibleChapter(
    val book: String,
    val chapter: Int,
    val verses: List<String>
)
