package mg.maniry.tenymana.helpers

import androidx.lifecycle.MutableLiveData
import mg.maniry.tenymana.gameLogic.linkClear.LinkClearPuzzle
import mg.maniry.tenymana.gameLogic.linkClear.SolutionItem
import mg.maniry.tenymana.gameLogic.models.*

class LinkClearPuzzleMock(
    override val verse: BibleVerse
) : LinkClearPuzzle {
    val proposeFn = Fn<Boolean>()
    val undoFn = Fn<Boolean>()
    val useBonusOneFn = Fn<List<Point>?>()
    override var grid: MutableGrid<Character> = MutableGrid(10)
    override var prevGrid: Grid<Character> = MutableGrid(10)
    override var diffs: List<Move>? = null
    override val cleared: List<Point>? = null
    override val score = MutableLiveData(0)
    override var completed = false
    override var solution = emptyList<SolutionItem<Character>>()

    override fun propose(move: Move) = proposeFn(move)
    override fun undo() = undoFn()
    override fun useBonusHintOne(price: Int) = useBonusOneFn(price)
    override fun useBonusRevealChars(n: Int, price: Int) = false
}
