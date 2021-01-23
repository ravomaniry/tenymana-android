package mg.maniry.tenymana.ui.views.verse

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import mg.maniry.tenymana.gameLogic.models.Character

class VerseViewControl : BaseVerseViewControl() {
    private val resolvedCharPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = H.toFloat()
    }
    private val bonusCharPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = H.toFloat()
        color = Color.WHITE
    }
    private val textDx = (W + SPACING_H) / 2
    private val textDy = H - SPACING_V

    override fun onColorsChange(primary: Int, accent: Int) {
        resolvedCharPaint.color = accent
        placeHolderPaint.color = primary
    }

    override fun draw(canvas: Canvas) {
        for (row in cells) {
            for (cell in row) {
                val w = words!![cell.char.wIndex]
                val char = w[cell.char.cIndex]
                if (w.resolved) {
                    canvas.drawChar(cell, char, resolvedCharPaint)
                } else {
                    if (char.resolved) {
                        canvas.drawChar(cell, char, bonusCharPaint)
                    }
                    canvas.drawPlaceHolder(cell)
                }
            }
        }
    }

    private fun Canvas.drawChar(
        cell: VerseViewCell,
        char: Character,
        paint: Paint
    ) {
        drawText(char.value.toString(), cell.x + textDx, cell.y + textDy, paint)
    }
}
