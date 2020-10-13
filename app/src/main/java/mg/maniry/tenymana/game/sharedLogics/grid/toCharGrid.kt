package mg.maniry.tenymana.game.sharedLogics.grid

import mg.maniry.tenymana.game.models.*

fun MutableGrid<CharAddress>.toCharGrid(words: List<Word>): Grid<Character> {
    return map { _, _, p ->
        if (p == null) null else words[p.wIndex].chars[p.cIndex].toGridChar()
    }
}
