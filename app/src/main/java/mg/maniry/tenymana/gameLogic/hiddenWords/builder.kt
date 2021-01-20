package mg.maniry.tenymana.gameLogic.hiddenWords

import mg.maniry.tenymana.gameLogic.models.BibleVerse
import mg.maniry.tenymana.gameLogic.models.Character
import mg.maniry.tenymana.gameLogic.models.Word
import mg.maniry.tenymana.utils.Random
import kotlin.math.max

fun buildHiddenWordsGroups(
    verse: BibleVerse,
    groupSize: Int,
    random: Random
): List<HiddenWordsGroup> {
    val groups = mutableListOf<HiddenWordsGroup>()
    var words = verse.uniqueWords
    val unique = verse.words.uniqueByChars()
    while (words.isNotEmpty()) {
        val activeWords = words.activeWords(groupSize)
        words = words.subList(activeWords.size, words.size).toList()
        groups.append(activeWords, unique, random)
    }
    return groups
}

private fun List<Word>.activeWords(groupSize: Int): List<Word> {
    return if (size <= groupSize + 1) this else subList(0, groupSize).toList()
}

private fun MutableList<HiddenWordsGroup>.append(
    words: List<Word>,
    unique: Set<Word>,
    random: Random
) {
    if (words.isNotEmpty()) {
        val hiddenIndex = words.calcHiddenIndex(unique, random)
        val chars = words.buildCharsList(hiddenIndex, random)
        add(HiddenWordsGroup(chars, words[hiddenIndex]))
    }
}

private fun List<Word>.calcHiddenIndex(unique: Set<Word>, random: Random): Int {
    val len = longestUniqueLen(unique)
    val indexes = mutableListOf<Int>()
    forEachIndexed { index, word ->
        if (unique.contains(word) && word.size >= len) {
            indexes.add(index)
        }
    }
    if (indexes.isEmpty()) {
        indexes.addAll(indices)
    }
    return random.from(indexes)
}

private fun List<Word>.uniqueByChars(): Set<Word> {
    val result = mutableSetOf<Word>()
    val visited = mutableListOf<Word>()
    forEach {
        if (!it.isSeparator) {
            val dup = visited.sameCharAs(it)
            if (dup == null) {
                result.add(it)
            } else {
                result.remove(dup)
            }
            visited.add(it)
        }
    }
    return result
}

private fun List<Word>.sameCharAs(other: Word): Word? {
    for (w in this) {
        if (w.sameChars(other.chars)) {
            return w
        }
    }
    return null
}

private fun List<Word>.longestUniqueLen(unique: Set<Word>): Int {
    var l = 0
    forEach {
        if (unique.contains(it)) {
            l = max(l, it.size)
        }
    }
    return l
}

private fun List<Word>.buildCharsList(hiddenIndex: Int, random: Random): List<Character> {
    val allChars = mutableListOf<Character>()
    forEachIndexed { index, word ->
        if (index != hiddenIndex) {
            allChars.addAll(word.chars)
        }
    }
    val chars = mutableListOf<Character>()
    while (allChars.isNotEmpty()) {
        val index = random.int(0, allChars.size - 1)
        chars.add(allChars[index].toGridChar())
        allChars.removeAt(index)
    }
    return chars
}
