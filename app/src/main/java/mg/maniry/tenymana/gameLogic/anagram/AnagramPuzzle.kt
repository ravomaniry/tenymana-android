package mg.maniry.tenymana.gameLogic.anagram

import androidx.lifecycle.MutableLiveData
import mg.maniry.tenymana.gameLogic.models.BibleVerse
import mg.maniry.tenymana.gameLogic.models.Character
import mg.maniry.tenymana.gameLogic.models.Puzzle
import mg.maniry.tenymana.gameLogic.models.Word
import mg.maniry.tenymana.gameLogic.shared.words.bonusRatio
import mg.maniry.tenymana.gameLogic.shared.words.deltaScore
import mg.maniry.tenymana.gameLogic.shared.words.revealChars
import mg.maniry.tenymana.utils.Random

interface AnagramPuzzle : Puzzle {
    val chars: List<Character?>
    fun propose(indexes: List<Int>): Boolean
    fun useBonus(n: Int, price: Int): Boolean

    companion object {
        fun build(verse: BibleVerse): AnagramPuzzle {
            val random = Random.impl()
            val chars = buildAnagram(verse, random)
            return AnagramPuzzleImpl(chars, verse, random)
        }
    }
}

class AnagramPuzzleImpl(
    private val allChars: List<List<Character>>,
    initialVerse: BibleVerse,
    private val random: Random
) : AnagramPuzzle {
    private var prevWords = initialVerse.words
    private val words = initialVerse.words.toMutableList()
    override val verse: BibleVerse = initialVerse.copy(words = words)

    private var _score = 0
    override val score = MutableLiveData(0)
    override var completed = false

    private var index = 0
    override var chars = allChars[0]

    override fun propose(indexes: List<Int>): Boolean {
        val selection = calcSelection(indexes)
        if (selection != null) {
            prevWords = words.toList()
            val didResolve = resolveWords(selection)
            if (didResolve) {
                words.resolveOneChars()
                next()
                updateScore()
                return true
            }
        }
        return false
    }

    override fun useBonus(n: Int, price: Int): Boolean {
        val didUpdate = words.revealChars(n, emptySet(), random)
        if (didUpdate) {
            _score -= price
            updateScore()
        }
        return didUpdate
    }

    private fun calcSelection(indexes: List<Int>): List<Character>? {
        if (indexes.size == chars.size) {
            return indexes.map { chars[it] }
        }
        return null
    }

    private fun resolveWords(selection: List<Character>): Boolean {
        var foundMatch = false
        words.forEachIndexed { i, word ->
            if (!word.isSeparator && !word.resolved && word.sameChars(selection)) {
                foundMatch = true
                words[i] = word.resolvedVersion
            }
        }
        return foundMatch
    }

    private fun next() {
        index++
        completed = index >= allChars.size
        if (!completed) {
            chars = allChars[index]
        }
    }

    private fun updateScore() {
        _score += words.deltaScore(prevWords)
        if (completed) {
            _score += (_score * words.bonusRatio).toInt()
        }
        score.postValue(_score)
    }
}

private fun MutableList<Word>.resolveOneChars() {
    for (i in indices) {
        val w = get(i)
        if (w.size == 1) {
            set(i, w.resolvedVersion)
        }
        if (!w.isSeparator && !w.resolved) {
            return
        }
    }
}
