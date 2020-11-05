package mg.maniry.tenymana.gameLogic.shared.bibleVerse

import mg.maniry.tenymana.gameLogic.models.BibleVerse
import mg.maniry.tenymana.gameLogic.models.Word

fun BibleVerse.calcStars(score: Int): Int {
    val charsN = words.countChars { !it.isSeparator }
    return when {
        score <= 0.5 * charsN -> 1
        score <= 1 * charsN -> 2
        else -> 3
    }
}

private fun List<Word>.countChars(filter: (Word) -> Boolean): Int {
    var count = 0
    forEach {
        if (filter(it)) {
            count += it.size
        }
    }
    return count
}
