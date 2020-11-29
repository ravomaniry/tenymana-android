package mg.maniry.tenymana.ui.views.verse

import android.graphics.Canvas
import android.graphics.Paint
import mg.maniry.tenymana.gameLogic.models.Word
import mg.maniry.tenymana.ui.views.settings.DrawingSettings
import kotlin.math.max

abstract class BaseVerseViewControl {
    var settings: DrawingSettings? = null
    private var width = 0
    protected var words: List<Word>? = null
    protected var cells: List<List<VerseViewCell>> = listOf()
    private var height = 0
    protected val placeHolderPaint = Paint().apply {
        style = Paint.Style.FILL
    }

    abstract fun onColorsChange(primary: Int, accent: Int)

    abstract fun draw(canvas: Canvas)

    fun onMeasure(w: Int) {
        width = w - PADDING * 2
        computeCells()
        updateHeight()
    }

    open fun onWordsChange(words: List<Word>?) {
        this.words = words
        computeCells()
        updateHeight()
    }

    private fun computeCells() {
        val next = mutableListOf<MutableList<VerseViewCell>>()
        cells = next
        if (words != null && width > 0) {
            var currentW = 0
            var rowI = 0
            var y = 0f
            next.add(mutableListOf())
            for (word in words!!) {
                val w = word.width
                if (w > width) {
                    currentW = next.appendAndWrap(word, y, width)
                    rowI = next.size - 1
                    y = rowI.toFloat() * LINE_H
                } else {
                    val totalW = currentW + w
                    if (totalW <= width) {
                        currentW = totalW
                    } else {
                        rowI++
                        y += H + SPACING_V
                        currentW = w
                        next.add(mutableListOf())
                    }
                    if (word.value != " " || currentW != w) {
                        next[rowI].append(words!!, word.index, y)
                    }
                }
            }
        }
    }

    private fun updateHeight() {
        if (settings != null) {
            val linesN = cells.size
            height = 2 * PADDING + H * linesN + SPACING_V * max(0, linesN - 1)
            settings!!.verseViewHeight = height
        }
    }

    protected fun drawPlaceHolder(canvas: Canvas, cell: VerseViewCell, dx: Float = 0f) {
        canvas.drawRect(cell.x + dx, cell.y, cell.x + W + dx, cell.y + H, placeHolderPaint)
    }

    companion object {
        const val W = 12
        const val H = 20
        const val SPACING_H = 2
        const val SPACING_V = 5
        const val PADDING = 5
        const val LINE_H = H + SPACING_V
    }
}

private val Word.width: Int
    get() {
        if (isSeparator) {
            return size * BaseVerseViewControl.W
        }
        var w = 0
        for (i in 0 until size) {
            w += BaseVerseViewControl.W
            if (i != size - 1) {
                w += BaseVerseViewControl.SPACING_H
            }
        }
        return w
    }

private fun Word.charWidthAt(i: Int): Int {
    return when {
        isSeparator || i == size - 1 -> BaseVerseViewControl.W
        else -> BaseVerseViewControl.W + BaseVerseViewControl.SPACING_H
    }
}

private fun MutableList<VerseViewCell>.append(words: List<Word>, wI: Int, y: Float) {
    val word = words[wI]
    var x = when {
        isEmpty() -> 0f
        else -> last().relativeX + words[wI - 1].charWidthAt(words[wI - 1].size - 1)
    }
    for (i in 0 until word.size) {
        add(VerseViewCell.relative(x, y, word.index, i))
        x += word.charWidthAt(i)
    }
}

private fun MutableList<MutableList<VerseViewCell>>.appendAndWrap(
    word: Word,
    y0: Float,
    width: Int
): Int {
    var y = y0
    if (isEmpty() || last().isNotEmpty()) {
        add(mutableListOf())
        y += BaseVerseViewControl.LINE_H
    }
    var row = last()
    var x = 0f
    for (i in 0 until word.size) {
        if (x + BaseVerseViewControl.W > width) {
            x = 0f
            y += BaseVerseViewControl.LINE_H
            row = mutableListOf()
            add(row)
        }
        row.add(VerseViewCell.relative(x, y, word.index, i))
        x += word.charWidthAt(i)
    }
    return (x + word.charWidthAt(word.size - 1)).toInt()
}
