package mg.maniry.tenymana.gameLogic.hiddenWords

import mg.maniry.tenymana.gameLogic.models.BibleVerse
import mg.maniry.tenymana.gameLogic.models.Character
import mg.maniry.tenymana.gameLogic.models.Word
import mg.maniry.tenymana.utils.Random
import kotlin.math.max
import kotlin.math.min

data class HiddenWordsGroup(
    val chars: List<Character>,
    val hidden: Word
)

fun buildHiddenWordsGroups(
    verse: BibleVerse,
    groupSize: Int,
    random: Random
): List<HiddenWordsGroup> {
    val groups = mutableListOf<HiddenWordsGroup>()
    var words = verse.uniqueWords
    while (words.isNotEmpty()) {
        val activeWords = words.subList(0, min(groupSize, words.size)).toList()
        words = words.subList(activeWords.size, words.size).toList()
        groups.append(activeWords, random)
    }
    return groups
}

private fun MutableList<HiddenWordsGroup>.append(words: List<Word>, random: Random) {
    if (words.isNotEmpty()) {
        val hiddenIndex = words.calcHiddenIndex(random)
        val chars = words.buildCharsList(hiddenIndex, random)
        add(HiddenWordsGroup(chars, words[hiddenIndex]))
    }
}

private fun List<Word>.calcHiddenIndex(random: Random): Int {
    val len = longestLen()
    val indexes = mutableListOf<Int>()
    forEachIndexed { index, word ->
        if (word.size == len) {
            indexes.add(index)
        }
    }
    return random.from(indexes)
}

private fun List<Word>.longestLen(): Int {
    var l = 0
    forEach {
        l = max(l, it.size)
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
