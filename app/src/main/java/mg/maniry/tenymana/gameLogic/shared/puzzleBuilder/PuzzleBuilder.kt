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

    override fun random(verse: BibleVerse): Puzzle {
        return when (randomType()) {
            GameTypes.LinkClear -> LinkClearPuzzle.build(verse)
            GameTypes.Anagram -> AnagramPuzzle.build(verse)
            else -> HiddenWordsPuzzle.build(verse)
        }
    }

    private fun randomType(): GameTypes {
        return random.from(
            listOf(
                GameTypes.LinkClear,
                GameTypes.HiddenWords,
                GameTypes.Anagram
            )
        )
    }
}
