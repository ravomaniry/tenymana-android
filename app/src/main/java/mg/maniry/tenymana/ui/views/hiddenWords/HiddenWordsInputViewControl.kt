package mg.maniry.tenymana.ui.views.hiddenWords

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import mg.maniry.tenymana.gameLogic.models.Character

typealias SelectHandler = (Int) -> Unit

class HiddenWordsInputViewControl : BaseHiddenWordsViewControl() {
    override val padding = PADDING
    override val cellWidth = CELL_SIZE
    override val cellHeight = CELL_SIZE
    override val marginV = CELL_MARGIN
    override val marginH = CELL_MARGIN
    private var selectHandler: SelectHandler? = null
    private val textPaint = Paint().apply {
        color = Color.WHITE
        isAntiAlias = true
        textAlign = Paint.Align.CENTER
        textSize = CELL_SIZE - 2
        textSize = CELL_SIZE - TEXT_PADDING * 2
    }
    private val availablePaint = Paint().apply {
        style = Paint.Style.FILL
    }
    private val usedPaint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.argb(40, 100, 100, 100)
    }

    override fun cellValueAccessor(character: Character?) = character?.value?.toString()

    fun onColorChange(color: Int) {
        availablePaint.color = color
    }

    fun onSelect(handler: SelectHandler) {
        selectHandler = handler
    }

    fun onClick(x: Int, y: Int) {
        val index = calcSelection(x, y)
        if (index != null && selectHandler != null) {
            selectHandler!!(index)
        }
    }

    override fun draw(canvas: Canvas) {
        for (row in cells) {
            for (cell in row) {
                canvas.drawCell(cell)
            }
        }
    }

    private fun Canvas.drawCell(cell: Cell) {
        val bgPaint = if (cell.value == null) usedPaint else availablePaint
        drawCellBg(cell.x, cell.y, CELL_SIZE, CELL_SIZE, bgPaint)
        if (cell.value != null) {
            val x = cell.x + CELL_SIZE / 2
            val y = cell.y + CELL_SIZE - TEXT_PADDING - 6
            drawCellText(x, y, cell.value, textPaint)
        }
    }

    private fun calcSelection(x: Int, y: Int): Int? {
        var i = 0
        for (row in cells) {
            if (row.isNotEmpty() && y >= row[0].y && y < row[0].y + CELL_SIZE) {
                for (cell in row) {
                    if (x >= cell.x && x < cell.x + CELL_SIZE) {
                        return i
                    }
                    i++
                }
            }
            i += row.size
        }
        return null
    }

    companion object {
        const val PADDING = 10F
        const val CELL_SIZE = 48F
        const val CELL_MARGIN = 10F
        const val TEXT_PADDING = 2f
    }
}
