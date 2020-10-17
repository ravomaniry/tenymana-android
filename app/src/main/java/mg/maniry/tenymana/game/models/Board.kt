package mg.maniry.tenymana.game.models

interface Board {
    val score: Int
    fun propose(move: Move): Boolean
}
