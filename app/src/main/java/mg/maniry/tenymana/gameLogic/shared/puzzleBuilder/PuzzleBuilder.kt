package mg.maniry.tenymana.gameLogic.shared.puzzleBuilder

import mg.maniry.tenymana.gameLogic.linkClear.LinkClearPuzzle
import mg.maniry.tenymana.gameLogic.models.BibleVerse
import mg.maniry.tenymana.utils.RandomImpl

interface PuzzleBuilder {
    fun linkClear(verse: BibleVerse): LinkClearPuzzle
}

class PuzzleBuilderImpl : PuzzleBuilder {
    override fun linkClear(verse: BibleVerse) = LinkClearPuzzle.build(verse, RandomImpl())
}
