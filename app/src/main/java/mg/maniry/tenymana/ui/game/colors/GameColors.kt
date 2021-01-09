package mg.maniry.tenymana.ui.game.colors

import mg.maniry.tenymana.R

interface GameColors {
    val primary: Int
    val accent: Int
}

class DefaultColors : GameColors {
    override val primary = R.color.blue
    override val accent = R.color.red
}

class LinkClearColors : GameColors {
    override val primary = R.color.red
    override val accent = R.color.blue
}

class HiddenWordsColors : GameColors {
    override val primary = R.color.blue
    override val accent = R.color.red
}

class AnagramColors : GameColors {
    override val primary = R.color.red
    override val accent = R.color.green
}
