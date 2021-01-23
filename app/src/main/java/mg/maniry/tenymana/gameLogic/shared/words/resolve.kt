package mg.maniry.tenymana.gameLogic.shared.words

import mg.maniry.tenymana.gameLogic.models.Character
import mg.maniry.tenymana.gameLogic.models.Word

fun MutableList<Word>.resolveWith(selection: List<Character>, hidden: Set<Int>): List<Int> {
    val resolved = mutableListOf<Int>()
    forEachIndexed { i, w ->
        if (!w.resolved && w.sameChars(selection)) {
            this[i] = w.resolvedVersion
            resolved.add(i)
        }
    }
    resolveHidden(hidden, resolved)
    return resolved
}

val List<Word>.resolved: Boolean get() = all { it.resolved }

private fun MutableList<Word>.resolveHidden(hidden: Set<Int>, resolved: MutableList<Int>) {
    for (i in hidden) {
        if (!this[i].resolved) {
            var shouldResolve = true
            val range = if (i == 0) 1 until size else (i - 1).downTo(0)
            for (otherI in range) {
                val other = this[otherI]
                if (!other.isSeparator) {
                    shouldResolve = other.resolved
                    break
                }
            }
            if (shouldResolve) {
                resolved.add(i)
                this[i] = this[i].resolvedVersion
            }
        }
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
