package mg.maniry.tenymana.gameLogic.shared.puzzleBuilder

import mg.maniry.tenymana.gameLogic.anagram.AnagramPuzzle
import mg.maniry.tenymana.gameLogic.hiddenWords.HiddenWordsPuzzle
import mg.maniry.tenymana.gameLogic.linkClear.LinkClearPuzzle
import mg.maniry.tenymana.gameLogic.models.BibleVerse
import mg.maniry.tenymana.gameLogic.models.Puzzle
import mg.maniry.tenymana.utils.Random

interface PuzzleBuilder {
    fun random(verse: BibleVerse): Puzzle
}

class PuzzleBuilderImpl(
    private val random: Random
) : PuzzleBuilder {
    enum class GameTypes {
        LinkClear,
        HiddenWords,
        Anagram
    }

    private var prevType: GameTypes? = null

    override fun random(verse: BibleVerse): Puzzle {
        return when (randomType()) {
            GameTypes.LinkClear -> LinkClearPuzzle.build(verse)
            GameTypes.Anagram -> AnagramPuzzle.build(verse)
            else -> HiddenWordsPuzzle.build(verse)
        }
    }

    private fun randomType(): GameTypes {
        if (prevType != null && random.double() > 0.85) {
            return prevType!!
        }
        val types = listOf(GameTypes.LinkClear, GameTypes.HiddenWords, GameTypes.Anagram)
            .filter { it != prevType }
        var minP = 0.0
        var type = GameTypes.LinkClear
        for (t in types) {
            val p = random.double()
            if (p > minP) {
                minP = p
                type = t
            }
        }
        prevType = type
        return type
    }
}
