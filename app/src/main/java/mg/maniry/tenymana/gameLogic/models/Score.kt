package mg.maniry.tenymana.gameLogic.models

data class Score(
    val value: Int,
    val stars: Int
) {
    companion object {
        val ZERO = Score(0, 0)
    }
}
