package mg.maniry.tenymana.gameLogic.shared.session

import mg.maniry.tenymana.repositories.models.Session

data class SessionPosition(
    val value: Session,
    val isCompleted: Boolean,
    val pathIndex: Int,
    val verseIndex: Int
)

fun Session.resume(): SessionPosition {
    var pathIndex = 0
    var verseIndex = 0
    for (i in journey.paths.indices) {
        val path = journey.paths[i]
        var remainingScore = if (progress.scores.size > i) progress.scores[i].size else 0
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
    return SessionPosition(this, false, pathIndex, verseIndex)
}

fun Session.resumePath(pathIndex: Int): SessionPosition {
    val verseIndex = when {
        hasScoreAt(pathIndex) && !pathIsCompletedAt(pathIndex) ->
            progress.scores[pathIndex].size
        else -> 0
    }
    return SessionPosition(
        this,
        isCompleted,
        pathIndex,
        verseIndex
    )
}

fun Session.canOpenVerse(pathIndex: Int, verseIndex: Int): Boolean {
    return verseIndex == 0 ||
            (progress.scores.size > pathIndex && progress.scores[pathIndex].size >= verseIndex)
}

private val Session.isCompleted: Boolean get() = progress.size >= journey.size

private fun Session.hasScoreAt(pathIndex: Int): Boolean {
    return progress.scores.size > pathIndex
}

private fun Session.pathIsCompletedAt(pathIndex: Int): Boolean {
    return progress.scores.size > pathIndex &&
            progress.scores[pathIndex].size >= journey.paths[pathIndex].size
}
