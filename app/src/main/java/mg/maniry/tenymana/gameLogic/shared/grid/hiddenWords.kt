package mg.maniry.tenymana.gameLogic.shared.grid

import mg.maniry.tenymana.gameLogic.models.CharAddress
import mg.maniry.tenymana.gameLogic.models.Grid
import mg.maniry.tenymana.gameLogic.models.Word

fun Grid<CharAddress>.calcHiddenWords(words: List<Word>): Set<Int> {
    val displayed = mutableSetOf<Int>()
    val hidden = mutableSetOf<Int>()
    forEach { _, _, address ->
        if (address != null) {
            displayed.add(address.wIndex)
        }
    }
    words.forEachIndexed { i, w ->
        if (
            !w.isSeparator &&
            !displayed.contains(i) &&
            !displayed.containsAnyOf(words.findAllMatches(w))
        ) {
            hidden.add(i)
        }
    }
    return hidden
}

private fun List<Word>.findAllMatches(word: Word): List<Int> {
    val list = mutableListOf<Int>()
    forEachIndexed { i, other ->
        if (i != word.index && word.sameChars(other.chars)) {
            list.add(i)
        }
    }
    return list
}

private fun Set<Int>.containsAnyOf(list: List<Int>): Boolean {
    for (i in list) {
        if (contains(i)) {
            return true
        }
    }
    return false
}
