package mg.maniry.tenymana.gameLogic.shared.session

import mg.maniry.tenymana.gameLogic.models.Puzzle
import mg.maniry.tenymana.gameLogic.models.Score
import mg.maniry.tenymana.gameLogic.shared.bibleVerse.calcStars
import mg.maniry.tenymana.repositories.models.Journey
import mg.maniry.tenymana.repositories.models.Path
import mg.maniry.tenymana.repositories.models.Progress
import mg.maniry.tenymana.repositories.models.Session

fun Session.next(pos: SessionPosition, puzzle: Puzzle): SessionPosition {
    val next = journey.nextVerse(pos)
    return if (next == null) {
        SessionPosition(
            copy(progress = progress.updateScore(pos, puzzle)),
            true,
            pos.pathIndex,
            pos.verseIndex
        )
    } else {
        SessionPosition(
            copy(progress = progress.updateScore(pos, puzzle)),
            false,
            next.first,
            next.second
        )
    }
}

private fun Journey.nextVerse(pos: SessionPosition): Pair<Int, Int>? {
    val path = paths[pos.pathIndex]
    return when {
        pos.verseIndex < path.maxIndex -> Pair(pos.pathIndex, pos.verseIndex + 1)
        pos.pathIndex + 1 < paths.size -> Pair(pos.pathIndex + 1, 0)
        else -> null
    }
}

private fun Progress.updateScore(pos: SessionPosition, puzzle: Puzzle): Progress {
    val score = puzzle.score.value ?: 0
    val nextScores = scores.toMutableList()
    nextScores.addUpTo(pos.pathIndex, ::listOf)
    val active = nextScores[pos.pathIndex].toMutableList()
    active.addUpTo(pos.verseIndex) { Score.ZERO }
    val stars = puzzle.verse.calcStars(score)
    active[pos.verseIndex] = Score(score, stars)
    nextScores[pos.pathIndex] = active
    return copy(scores = nextScores, totalScore = nextScores.total)
}

private val Path.maxIndex: Int get() = end - start

private fun <T> MutableList<T>.addUpTo(n: Int, builder: () -> T) {
    while (size <= n) {
        add(builder())
    }
}

private val List<List<Score>>.total: Int
    get() {
        var n = 0
        forEach { pathScores ->
            pathScores.forEach {
                n += it.value
            }
        }
        return n
    }
