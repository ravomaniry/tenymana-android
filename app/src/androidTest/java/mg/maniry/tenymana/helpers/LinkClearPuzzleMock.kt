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
    override var diff = MutableLiveData<List<Move>?>()
    override val cleared = MutableLiveData<List<Point>?>()
    override val score = MutableLiveData(0)
    override var completed = false
    override var solution = emptyList<SolutionItem<Character>>()

    override fun propose(move: Move) = proposeFn(move)
    override fun undo() = undoFn()
    override fun useBonusOne(price: Int) = useBonusOneFn(price)
}
