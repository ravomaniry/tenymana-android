package mg.maniry.tenymana.gameLogic.hiddenWords

import androidx.lifecycle.MutableLiveData
import mg.maniry.tenymana.gameLogic.models.BibleVerse
import mg.maniry.tenymana.gameLogic.models.Character
import mg.maniry.tenymana.gameLogic.models.Puzzle
import mg.maniry.tenymana.gameLogic.models.Word
import mg.maniry.tenymana.gameLogic.shared.chars.hasMatch
import mg.maniry.tenymana.gameLogic.shared.words.resolveWith
import mg.maniry.tenymana.gameLogic.shared.words.resolved
import mg.maniry.tenymana.utils.Random
import mg.maniry.tenymana.utils.findIndex
import kotlin.math.max

interface HiddenWordsPuzzle : Puzzle {
    val groups: List<HiddenWordsGroup>
    val firstGroup: Int
    fun propose(groupIndex: Int, charsIndexes: List<Int>): Boolean

    companion object {
        fun build(verse: BibleVerse, groupSize: Int, random: Random): HiddenWordsPuzzle {
            val groups = buildHiddenWordsGroups(verse, groupSize, random)
            return HiddenWordsPuzzleImpl(verse, groups)
        }
    }
}

class HiddenWordsPuzzleImpl(
    initialVerse: BibleVerse,
    initialGroups: List<HiddenWordsGroup>
) : HiddenWordsPuzzle {
    private var _score = 0
    override val score = MutableLiveData(0)
    override var completed = false
    override var firstGroup: Int = 0
    override val groups = initialGroups.toMutableList()
    private val words = initialVerse.words.toMutableList()
    override val verse: BibleVerse = initialVerse.copy(words = words)
    private val matches = MutableList(groups.size) { true }

    override fun propose(groupIndex: Int, charsIndexes: List<Int>): Boolean {
        val chars = groups[groupIndex].toChars(charsIndexes)
        if (chars.isNotEmpty()) {
            val wIndexes = words.resolveWith(chars, emptySet())
            if (wIndexes.isNotEmpty()) {
                val shown = groups.resolve(groupIndex, charsIndexes)
                resolveHiddenW(shown)
                matches.updateWith(words, groups)
                completed = matches.allFalse
                firstGroup = matches.firstTrue
                updateScore()
                return true
            }
        }
        return false
    }

    private fun resolveHiddenW(shown: Word?) {
        if (shown != null) {
            for (i in 0 until words.size) {
                val w = words[i]
                if (!w.resolved && w == shown) {
                    words[i] = w.resolvedVersion
                }
            }
        }
    }

    private fun updateScore() {
        _score = 0
        for (w in words) {
            if (!w.isSeparator && w.resolved) {
                _score += w.size
            }
        }
        if (completed && words.resolved) {
            _score *= 2
        }
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

private val List<Boolean>.allFalse: Boolean get() = all { !it }

private val List<Boolean>.firstTrue: Int get() = max(0, findIndex { it })

private fun MutableList<Boolean>.updateWith(words: List<Word>, groups: List<HiddenWordsGroup>) {
    for (i in indices) {
        set(i, groups[i].chars.hasMatch(words))
    }
}
