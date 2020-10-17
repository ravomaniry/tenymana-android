package mg.maniry.tenymana.game.sharedLogic.grid

import mg.maniry.tenymana.game.models.*

fun Grid<CharAddress>.toCharGrid(words: List<Word>): MutableGrid<Character> {
    val grid = MutableGrid<Character>(w)
    forEach { x, y, address ->
        val c = when (address) {
            null -> null
            else -> words[address.wIndex].chars[address.cIndex].toGridChar()
        }
        grid.set(x, y, c)
    }
    return grid
}
