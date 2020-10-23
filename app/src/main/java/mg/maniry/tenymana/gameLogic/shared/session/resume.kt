package mg.maniry.tenymana.gameLogic.shared.session

import mg.maniry.tenymana.repositories.models.Session

data class SessionStep(
    val pathIndex: Int,
    val verseIndex: Int
)

fun Session.resume(): SessionStep {
    var pathIndex = 0
    var verseIndex = 0
    var remainingScore = progress.scores.size
    for (path in journey.paths) {
        val len = path.end - path.start + 1
        if (remainingScore >= len) {
            remainingScore -= len
            pathIndex++
        } else {
            verseIndex = remainingScore
            break
        }
    }
    if (pathIndex >= journey.paths.size) {
        pathIndex = 0
        verseIndex = 0
    }
    return SessionStep(pathIndex, verseIndex)
}
