package mg.maniry.tenymana.ui.views.charsGrid

import android.graphics.Canvas

class CharGridViewControl : BaseCharGridControl() {
    override fun draw(canvas: Canvas) {
        if (grid != null) {
            for (y in 0 until boardHeight) {
                for (x in 0 until grid!!.w) {
                    val char = grid!![x, y]
                    if (char == null) {
                        drawEmptyBG(canvas, x, y, 0, 0)
                    } else {
                        drawFilledBG(canvas, x, y, 0, 0)
                        drawChar(canvas, char.value, x, y, 0, 0)
                    }
                }
            }
        }
    }
}
