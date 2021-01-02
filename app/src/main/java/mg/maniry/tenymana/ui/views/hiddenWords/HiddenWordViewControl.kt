package mg.maniry.tenymana.ui.views.hiddenWords

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import mg.maniry.tenymana.gameLogic.models.Character

class HiddenWordViewControl : BaseHiddenWordsViewControl() {
    var resolved = false
    override val padding = PADDING
    override val cellWidth = WIDTH
    override val cellHeight = HEIGHT
    override val marginV = MARGIN_V
    override val marginH = MARGIN_H

    private val bgPaint = Paint()
    private val fgPaint = Paint()
    private val textPaint = Paint().apply {
        isAntiAlias = true
        color = Color.WHITE
    }

    override fun cellValueAccessor(character: Character?): String? {
        return if (resolved) character?.value?.toString() else null
    }

    fun onColorsChange(fg: Int, bg: Int) {
        fgPaint.color = fg
        bgPaint.color = bg
    }

    override  fun draw(canvas: Canvas) {
        for (row in cells) {
            for (cell in row) {
                canvas.drawCell(cell)
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
