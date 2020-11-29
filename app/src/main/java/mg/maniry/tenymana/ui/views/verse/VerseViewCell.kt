package mg.maniry.tenymana.ui.views.verse

import mg.maniry.tenymana.gameLogic.models.CharAddress

data class VerseViewCell constructor(
    val x: Float,
    val y: Float,
    val char: CharAddress
) {
    val relativeX: Float get() = x - BaseVerseViewControl.PADDING

    companion object {
        fun relative(x: Float, y: Float, wI: Int, cI: Int): VerseViewCell {
            return VerseViewCell(
                x + BaseVerseViewControl.PADDING,
                y + BaseVerseViewControl.PADDING,
                CharAddress(wI, cI)
            )
        }
    }
}
