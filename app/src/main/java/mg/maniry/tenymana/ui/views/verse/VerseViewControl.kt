package mg.maniry.tenymana.ui.views.verse

import android.graphics.Canvas
import android.graphics.Paint
import mg.maniry.tenymana.gameLogic.models.Character

class VerseViewControl : BaseVerseViewControl() {
    private val charPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = H.toFloat()
    }
    private val textDx = (W + SPACING_H) / 2
    private val textDy = H - SPACING_V

    override fun onColorsChange(primary: Int, accent: Int) {
        charPaint.color = accent
        placeHolderPaint.color = primary
    }

    override fun draw(canvas: Canvas) {
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

    private fun drawChar(canvas: Canvas, cell: VerseViewCell, char: Character) {
        canvas.drawText(char.value.toString(), cell.x + textDx, cell.y + textDy, charPaint)
    }
}
