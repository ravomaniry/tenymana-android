package mg.maniry.tenymana.gameLogic.shared.puzzleBuilder

import mg.maniry.tenymana.gameLogic.hiddenWords.HiddenWordsPuzzle
import mg.maniry.tenymana.gameLogic.linkClear.LinkClearPuzzle
import mg.maniry.tenymana.gameLogic.models.BibleVerse
import mg.maniry.tenymana.gameLogic.models.Puzzle
import mg.maniry.tenymana.utils.RandomImpl
import kotlin.math.floor

interface PuzzleBuilder {
    fun random(verse: BibleVerse): Puzzle
}

class PuzzleBuilderImpl : PuzzleBuilder {
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
        val types = listOf(
            GameTypes.LinkClear,
            GameTypes.HiddenWords
        )
        return types[floor(Math.random() * (types.size - 1)).toInt()]
    }
}
