package mg.maniry.tenymana.repositories.models

import mg.maniry.tenymana.gameLogic.models.Bonus

data class Session(
    val journey: Journey,
    val progress: Progress
)

data class Progress(
    val journeyID: String,
    val totalScore: Int,
    val scores: List<List<Int>>,
    val bonuses: List<Bonus>
) {
    companion object {
        fun empty(id: String) = Progress(id, 0, emptyList(), emptyList())
    }
}
