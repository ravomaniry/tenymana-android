package mg.maniry.tenymana.ui.views.verse

import android.graphics.Canvas
import android.graphics.Paint
import mg.maniry.tenymana.gameLogic.models.CharAddress
import mg.maniry.tenymana.gameLogic.models.Character
import mg.maniry.tenymana.gameLogic.models.Word
import mg.maniry.tenymana.ui.views.settings.DrawingSettings
import kotlin.math.max

class VerseViewControl {
    var settings: DrawingSettings? = null
    private var width = 0
    private var words: List<Word>? = null
    private var cells: List<List<Cell>> = listOf()
    private var primaryColor: Int = 0
    private var accentColor: Int = 0

    private val placeHolderPaint = Paint().apply {
        style = Paint.Style.FILL
    }
    private val charPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = H.toFloat()
    }

    private var height = 0
    private val textDx = (W + SPACING_H) / 2
    private val textDy = H - SPACING_V

    fun onMeasure(w: Int) {
        width = w - PADDING * 2
        computeCells()
        updateHeight()
    }

    fun onWordsChange(words: List<Word>?) {
        this.words = words
        computeCells()
        updateHeight()
    }

    fun onColorsChange(primary: Int, accent: Int) {
        primaryColor = primary
        accentColor = accent
        updatePaints()
    }

    private fun computeCells() {
        val next = mutableListOf<MutableList<Cell>>()
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

    private fun updatePaints() {
        placeHolderPaint.color = primaryColor
        charPaint.color = accentColor
    }

    fun draw(canvas: Canvas) {
        for (row in cells) {
            for (cell in row) {
                val w = words!![cell.char.wIndex]
                val char = w[cell.char.cIndex]
                if (w.resolved || char.resolved) {
                    drawChar(canvas, cell, char)
                } else {
                    drawPlaceHolder(canvas, cell)
                }
            }
        }
    }

    private fun drawPlaceHolder(canvas: Canvas, cell: Cell) {
        canvas.drawRect(cell.x, cell.y, cell.x + W, cell.y + H, placeHolderPaint)
    }

    private fun drawChar(canvas: Canvas, cell: Cell, char: Character) {
        canvas.drawText(char.value.toString(), cell.x + textDx, cell.y + textDy, charPaint)
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


private data class Cell constructor(
    val x: Float,
    val y: Float,
    val char: CharAddress
) {
    val relativeX: Float get() = x - VerseViewControl.PADDING

    companion object {
        fun relative(x: Float, y: Float, wI: Int, cI: Int): Cell {
            return Cell(
                x + VerseViewControl.PADDING,
                y + VerseViewControl.PADDING,
                CharAddress(wI, cI)
            )
        }
    }
}

private val Word.width: Int
    get() {
        if (isSeparator) {
            return size * VerseViewControl.W
        }
        var w = 0
        for (i in 0 until size) {
            w += VerseViewControl.W
            if (i != size - 1) {
                w += VerseViewControl.SPACING_H
            }
        }
        return w
    }

private fun Word.charWidthAt(i: Int): Int {
    return when {
        isSeparator || i == size - 1 -> VerseViewControl.W
        else -> VerseViewControl.W + VerseViewControl.SPACING_H
    }
}

private fun MutableList<Cell>.append(words: List<Word>, wI: Int, y: Float) {
    val word = words[wI]
    var x = when {
        isEmpty() -> 0f
        else -> last().relativeX + words[wI - 1].charWidthAt(words[wI - 1].size - 1)
    }
    for (i in 0 until word.size) {
        add(Cell.relative(x, y, word.index, i))
        x += word.charWidthAt(i)
    }
}

private fun MutableList<MutableList<Cell>>.appendAndWrap(word: Word, y0: Float, width: Int): Int {
    var y = y0
    if (isEmpty() || last().isNotEmpty()) {
        add(mutableListOf())
        y += VerseViewControl.LINE_H
    }
    var row = last()
    var x = 0f
    for (i in 0 until word.size) {
        if (x + VerseViewControl.W > width) {
            x = 0f
            y += VerseViewControl.LINE_H
            row = mutableListOf()
            add(row)
        }
        row.add(Cell.relative(x, y, word.index, i))
        x += word.charWidthAt(i)
    }
    return (x + word.charWidthAt(word.size - 1)).toInt()
}
