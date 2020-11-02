package mg.maniry.tenymana.helpers

import mg.maniry.tenymana.gameLogic.linkClear.LinkClearPuzzle
import mg.maniry.tenymana.gameLogic.models.BibleVerse
import mg.maniry.tenymana.gameLogic.shared.puzzleBuilder.PuzzleBuilder

class PuzzleBuilderMock : PuzzleBuilder {
    val linkClearFn = Fn<LinkClearPuzzle>()
    override fun linkClear(verse: BibleVerse) = linkClearFn(verse)
}
