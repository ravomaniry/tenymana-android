package mg.maniry.tenymana.helpers

import mg.maniry.tenymana.gameLogic.linkClear.LinkClearPuzzle
import mg.maniry.tenymana.gameLogic.models.*

class LinkClearPuzzleMock(
    override val verse: BibleVerse
) : LinkClearPuzzle {
    val proposeFn = Fn<Boolean>()
    override var grid: MutableGrid<Character> = MutableGrid(10)
    override var diff: List<Move>? = null
    override var cleared: List<Point>? = null
    override var score = 0
    override var completed = false

    override fun propose(move: Move) = proposeFn(move)
}
