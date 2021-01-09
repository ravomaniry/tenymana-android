package mg.maniry.tenymana.gameLogic.anagram

import mg.maniry.tenymana.gameLogic.models.BibleVerse
import mg.maniry.tenymana.gameLogic.models.Character
import mg.maniry.tenymana.utils.Random

fun buildAnagram(verse: BibleVerse, random: Random): List<List<Character>> {
    val result = mutableListOf<List<Character>>()
    val words = verse.uniqueWords
        .filter { it.size > 1 }
        .shuffle(random)
    for (word in words) {
        val chars = word.chars.shuffle(random)
            .map { it.toGridChar() }
        result.add(chars)
    }
    return result
}

private fun <T> List<T>.shuffle(random: Random): List<T> {
    val remaining = toMutableList()
    val result = mutableListOf<T>()
    while (remaining.isNotEmpty()) {
        val i = random.int(0, remaining.size - 1)
        result.add(remaining[i])
        remaining.removeAt(i)
    }
    return result
}
