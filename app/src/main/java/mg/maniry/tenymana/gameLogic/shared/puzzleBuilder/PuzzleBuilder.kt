package mg.maniry.tenymana.gameLogic.shared.puzzleBuilder

import mg.maniry.tenymana.gameLogic.linkClear.LinkClearPuzzle
import mg.maniry.tenymana.gameLogic.models.BibleVerse
import mg.maniry.tenymana.gameLogic.models.Puzzle
import mg.maniry.tenymana.utils.RandomImpl

interface PuzzleBuilder {
    fun random(verse: BibleVerse): Puzzle
}

class PuzzleBuilderImpl : PuzzleBuilder {
    override fun random(verse: BibleVerse) = LinkClearPuzzle.build(verse, RandomImpl())
}
