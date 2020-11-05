package mg.maniry.tenymana.repositories.models

import mg.maniry.tenymana.gameLogic.models.Bonus
import mg.maniry.tenymana.gameLogic.models.Score
import mg.maniry.tenymana.utils.countSubItems

data class Session(
    val journey: Journey,
    val progress: Progress
)

data class Progress(
    val journeyID: String,
    val totalScore: Int = 0,
    val scores: List<List<Score>> = emptyList(),
    val bonuses: List<Bonus> = emptyList()
) {
    val size: Int = scores.countSubItems()
}
