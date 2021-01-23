package mg.maniry.tenymana.gameLogic.shared.words

import mg.maniry.tenymana.gameLogic.models.Word
import mg.maniry.tenymana.utils.Random

fun MutableList<Word>.revealChars(n: Int, hidden: Set<Word>, random: Random): Boolean {
    if (maxUsableBonus(hidden) >= n) {
        revealRandomChars(n, hidden, random)
        return true
    }
    return false
}

private fun List<Word>.maxUsableBonus(hidden: Set<Word>): Int {
    var n = 0
    for (w in this) {
        if (!w.isSeparator && !w.resolved && !hidden.contains(w)) {
            val avail = w.unresolvedChar()
            if (avail.size > 1) {
                n += avail.size - 1
            }
        }
    }
    return n
}

private fun MutableList<Word>.revealRandomChars(n: Int, hidden: Set<Word>, random: Random) {
    var remaining = n
    while (remaining > 0) {
        val wIndexes = mutableListOf<Int>()
        val cIndexes = hashMapOf<Int, List<Int>>()
        for (w in this) {
            if (!w.isSeparator && !w.resolved && !hidden.contains(w)) {
                val cI = w.unresolvedChar()
                if (cI.size > 1) {
                    wIndexes.add(w.index)
                    cIndexes[w.index] = cI
                }
            }
        }
        val wI = random.from(wIndexes)
        val cI = random.from(cIndexes[wI]!!)
        val nextChars = get(wI).chars.toMutableList()
        nextChars[cI] = nextChars[cI].copy(resolved = true)
        set(wI, get(wI).copy(chars = nextChars))
        remaining--
    }
}

fun Word.unresolvedChar(): List<Int> {
    val indexes = mutableListOf<Int>()
    chars.forEachIndexed { i, c ->
        if (!c.resolved) {
            indexes.add(i)
        }
    }
    return indexes
}
