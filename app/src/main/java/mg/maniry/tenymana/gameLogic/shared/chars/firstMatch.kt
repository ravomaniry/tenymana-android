package mg.maniry.tenymana.gameLogic.shared.chars

import mg.maniry.tenymana.gameLogic.models.Character
import mg.maniry.tenymana.gameLogic.models.Word
import mg.maniry.tenymana.utils.findIndex

fun List<Character?>.firstMatch(words: List<Word>): Int {
    val visited = mutableSetOf<Character>()
    val noNull = toChars()
    for (c in noNull) {
        if (!visited.contains(c)) {
            visited.add(c)
            for (i in words.indices) {
                val w = words[i]
                if (w.startsWith(c) && noNull.canBuild(w)) {
                    return i
                }
            }
        }
    }
    return -1
}

private fun List<Character?>.toChars(): List<Character> {
    val chars = mutableListOf<Character>()
    for (c in this) {
        if (c != null) {
            chars.add(c)
        }
    }
    return chars
}

private fun List<Character>.canBuild(word: Word): Boolean {
    if (size < word.size) {
        return false
    }
    val remaining = toMutableList()
    for (c in word.chars) {
        val i = remaining.findIndex { it.isSameAs(c) }
        if (i == -1) {
            return false
        } else {
            remaining.removeAt(i)
        }
    }
    return true
}
