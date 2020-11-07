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
