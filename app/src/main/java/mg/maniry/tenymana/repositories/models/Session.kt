package mg.maniry.tenymana.repositories.models

import mg.maniry.tenymana.gameLogic.models.Bonus

data class Session(
    val journey: Journey,
    val progress: Progress
)

data class Progress(
    val journeyID: String,
    val totalScore: Int = 0,
    val scores: List<List<Int>> = emptyList(),
    val bonuses: List<Bonus> = emptyList()
)
