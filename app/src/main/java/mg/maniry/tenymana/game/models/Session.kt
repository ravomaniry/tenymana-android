package mg.maniry.tenymana.game.models

data class Session(
    val journey: Journey,
    val progress: Progress
)

data class Progress(
    val journeyID: String,
    val totalScore: Int,
    val scores: List<Int>,
    val bonuses: List<Bonus>
) {
    companion object {
        fun empty(id: String) = Progress(id, 0, emptyList(), emptyList())
    }
}
