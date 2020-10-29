package mg.maniry.tenymana.gameLogic.models

interface Puzzle {
    val score: Int
    val completed: Boolean
    val verse: BibleVerse
    fun propose(move: Move): Boolean
}
