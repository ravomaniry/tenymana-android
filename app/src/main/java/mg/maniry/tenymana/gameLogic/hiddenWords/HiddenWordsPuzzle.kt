package mg.maniry.tenymana.gameLogic.hiddenWords

import androidx.lifecycle.MutableLiveData
import mg.maniry.tenymana.gameLogic.models.BibleVerse
import mg.maniry.tenymana.gameLogic.models.Character
import mg.maniry.tenymana.gameLogic.models.Puzzle
import mg.maniry.tenymana.gameLogic.models.Word
import mg.maniry.tenymana.gameLogic.shared.words.resolveWith

interface HiddenWordsPuzzle : Puzzle {
    val groups: List<HiddenWordsGroup>
    fun propose(groupIndex: Int, charsIndexes: List<Int>): Boolean
}

class HiddenWordsPuzzleImpl(
    initialVerse: BibleVerse,
    initialGroups: List<HiddenWordsGroup>
) : HiddenWordsPuzzle {
    private var _score = 0
    override val score = MutableLiveData(0)
    override val completed = false
    private val words = initialVerse.words.toMutableList()
    override val verse: BibleVerse = initialVerse.copy(words = words)
    override val groups = initialGroups.toMutableList()

    override fun propose(groupIndex: Int, charsIndexes: List<Int>): Boolean {
        val chars = groups[groupIndex].toChars(charsIndexes)
        if (chars.isNotEmpty()) {
            val wIndexes = words.resolveWith(chars, emptySet())
            if (wIndexes.isNotEmpty()) {
                incrementScore(wIndexes)
                val shown = groups.resolve(groupIndex, charsIndexes)
                resolveHiddenW(shown)
                return true
            }
        }
        return false
    }

    private fun incrementScore(wIndexes: List<Int>) {
        for (i in wIndexes) {
            _score += words[i].size
        }
        syncScore()
    }

    private fun resolveHiddenW(shown: Word?) {
        if (shown != null) {
            for (i in 0 until words.size) {
                val w = words[i]
                if (!w.resolved && w == shown) {
                    _score += w.size
                    words[i] = w.resolvedVersion
                }
            }
            syncScore()
        }
    }

    private fun syncScore() {
        if (_score != score.value) {
            score.postValue(_score)
        }
    }
}

private fun HiddenWordsGroup.toChars(indexes: List<Int>): List<Character> {
    val result = mutableListOf<Character>()
    for (i in indexes) {
        val c = chars[i]
        if (c != null) {
            result.add(c)
        }
    }
    return result
}

private fun MutableList<HiddenWordsGroup>.resolve(
    groupIndex: Int,
    charsIndexes: List<Int>
): Word? {
    val group = get(groupIndex)
    val nextChars = group.chars.toMutableList()
    for (i in charsIndexes) {
        nextChars[i] = null
    }
    val resolved = nextChars.find { it != null } == null
    set(groupIndex, group.copy(chars = nextChars, resolved = resolved))
    return if (resolved) group.hidden else null
}
