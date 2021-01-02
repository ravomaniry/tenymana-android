package mg.maniry.tenymana.gameLogic.shared.puzzleBuilder

import mg.maniry.tenymana.gameLogic.hiddenWords.HiddenWordsPuzzle
import mg.maniry.tenymana.gameLogic.linkClear.LinkClearPuzzle
import mg.maniry.tenymana.gameLogic.models.BibleVerse
import mg.maniry.tenymana.gameLogic.models.Puzzle
import mg.maniry.tenymana.utils.Random
import mg.maniry.tenymana.utils.RandomImpl

interface PuzzleBuilder {
    fun random(verse: BibleVerse): Puzzle
}

class PuzzleBuilderImpl(
    private val random: Random
) : PuzzleBuilder {
    enum class GameTypes {
        LinkClear,
        HiddenWords
    }

    override fun random(verse: BibleVerse): Puzzle {
        return when (randomType()) {
            GameTypes.LinkClear -> LinkClearPuzzle.build(verse, RandomImpl())
            else -> HiddenWordsPuzzle.build(
                verse,
                HiddenWordsPuzzle.IN_GAME_GROUP_SIZE,
                RandomImpl()
            )
        }
    }

    private fun randomType(): GameTypes {
        return random.from(
            listOf(
                GameTypes.LinkClear,
                GameTypes.HiddenWords
            )
        )
    }
}
