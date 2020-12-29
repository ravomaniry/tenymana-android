package mg.maniry.tenymana.ui.views.hiddenWords

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import mg.maniry.tenymana.gameLogic.models.Character

private data class Cell(
    val x: Float,
    val y: Float,
    val value: String?
)

class HiddenWordViewControl {
    var word: List<Character> = emptyList()
        set(value) {
            field = value
            updateHeight()
        }
    var resolved = false
    private var viewWidth = 0
    private var cells = mutableListOf<MutableList<Cell>>()
    private var _height = PADDING * 2
    val height: Int get() = _height.toInt()
    private val bgPaint = Paint()
    private val fgPaint = Paint()
    private val textPaint = Paint().apply {
        isAntiAlias = true
        color = Color.WHITE
    }

    fun onColorsChange(fg: Int, bg: Int) {
        fgPaint.color = fg
        bgPaint.color = bg
    }

    fun onMeasure(width: Int) {
        viewWidth = width
        updateHeight()
    }

    fun draw(canvas: Canvas) {
        for (row in cells) {
            for (cell in row) {
                canvas.drawCell(cell)
            }
        }
    }

    private fun updateHeight() {
        computeCells()
        centerCells()
        val margins = if (cells.size == 0) 0f else (cells.size - 1) * MARGIN_V
        _height = cells.size * HEIGHT + PADDING * 2 + margins
    }

    private fun computeCells() {
        cells = mutableListOf()
        if (word.isNotEmpty()) {
            val drawingWidth = viewWidth - PADDING * 2
            var x = drawingWidth + 1
            var y = 0f
            for (char in word) {
                if (x + WIDTH > drawingWidth) {
                    cells.add(mutableListOf())
                    x = 0f
                    y += HEIGHT + MARGIN_V
                }
                val value = if (resolved) char.value.toString() else null
                cells.last().add(
                    Cell(
                        x,
                        y + PADDING - HEIGHT - MARGIN_V,
                        value
                    )
                )
                x += WIDTH + MARGIN_H
            }
        }
    }

    private fun centerCells() {
        for (row in cells) {
            val w = row.size * WIDTH + (row.size - 1) * MARGIN_H
            val padding = (viewWidth - w) / 2
            for (i in row.indices) {
                row[i] = row[i].copy(x = row[i].x + padding)
            }
        }
    }

    private fun Canvas.drawCell(cell: Cell) {
        drawCellRect(cell.x + BG_OFFSET, cell.y + BG_OFFSET, null, bgPaint)
        drawCellRect(cell.x, cell.y, cell.value, fgPaint, textPaint)
    }

    private fun Canvas.drawCellRect(
        x: Float,
        y: Float,
        value: String?,
        bgPaint: Paint,
        txtPaint: Paint? = null
    ) {
        drawRect(x, y, x + WIDTH, y + HEIGHT, bgPaint)
        if (value != null && txtPaint != null) {
            drawText(value, x + WIDTH / 2, y, txtPaint)
        }
    }

    companion object {
        const val BG_OFFSET = 4
        const val PADDING = 10f
        const val HEIGHT = 48f
        const val WIDTH = 32f
        const val MARGIN_H = 8f
        const val MARGIN_V = 10f
    }
}
