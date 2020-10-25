package mg.maniry.tenymana.ui.game.puzzle.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import mg.maniry.tenymana.gameLogic.models.CharAddress
import mg.maniry.tenymana.gameLogic.models.Word

class VerseView : View {
    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) :
            super(context, attributeSet, defStyleAttr)

    private val brain = VerseViewBrain()

    fun onWordsChange(words: List<Word>?) {
        brain.onWordsChange(words)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val w = MeasureSpec.getSize(widthMeasureSpec)
        brain.onMeasure(w)
        setMeasuredDimension(w, brain.height)
    }
}

class VerseViewBrain {
    private var width = 0
    private var words: List<Word>? = null
    private var cells: List<List<Cell>> = listOf()

    val height: Int get() = (H + SPACING_V) * cells.size + SPACING_V

    fun onMeasure(w: Int) {
        width = w
        computeCells()
    }

    fun onWordsChange(words: List<Word>?) {
        this.words = words
        computeCells()
    }

    private fun computeCells() {
        val next = mutableListOf<MutableList<Cell>>()
        cells = next
        if (words != null && width > 0) {
            var currentW = 0
            var rowI = 0
            var y = SPACING_V
            next.add(mutableListOf())
            words!!.forEach {
                val w = it.width
                val totalW = currentW + w
                if (totalW <= width) {
                    currentW = totalW
                } else {
                    rowI++
                    y += H + SPACING_V
                    currentW = w
                    next.add(mutableListOf())
                }
                next[rowI].append(it, y)
            }
        }
    }

    companion object {
        const val W = 16
        const val H = 20
        const val SPACING_H = 4
        const val SPACING_V = 5
    }
}

private data class Cell(
    val x: Int,
    val y: Int,
    val char: CharAddress
)

private val Word.width: Int
    get() {
        if (isSeparator) {
            return size * VerseViewBrain.W
        }
        var w = 0
        for (i in 0 until size) {
            w += VerseViewBrain.W
            if (i != size - 1) {
                w += VerseViewBrain.SPACING_H
            }
        }
        return w
    }

private fun MutableList<Cell>.append(word: Word, y: Int) {
    var x = 0
    for (i in 0 until word.size) {
        add(Cell(x, y, CharAddress(word.index, i)))
        x += VerseViewBrain.W + VerseViewBrain.SPACING_H
    }
}
