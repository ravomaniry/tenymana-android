package mg.maniry.tenymana.gameLogic.shared.words

import mg.maniry.tenymana.gameLogic.models.Word

val List<Word>.bonusRatio: Double
    get() {
        var count = 0.0
        var resolvedCount = 0.0
        forEach {
            if (!it.isSeparator) {
                count++
                if (it.resolved) {
                    resolvedCount++
                }
            }
        }
        return if (count > 0) resolvedCount / count else 0.0
    }

fun List<Word>.deltaScore(prev: List<Word>): Int {
    var ds = 0
    forEachIndexed { i, word ->
        if (!word.isSeparator && word.resolved && !prev[i].resolved) {
            ds += word.size
        }
    }
    return ds
}
