package mg.maniry.tenymana.ui.game.colors

import mg.maniry.tenymana.R

interface GameColors {
    val primary: Int
    val accent: Int
}

object DefaultColor : GameColors {
    override val primary = R.color.blue
    override val accent = R.color.red
}
