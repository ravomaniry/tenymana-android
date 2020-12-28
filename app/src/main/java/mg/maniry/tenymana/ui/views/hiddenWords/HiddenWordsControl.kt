package mg.maniry.tenymana.ui.views.hiddenWords

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import mg.maniry.tenymana.gameLogic.models.Character

private data class Cell(
    val x: Int,
    val y: Int,
    val value: String?
)

class HiddenWordsControl {
    var word: List<Character?> = emptyList()
        set(value) {
            field = value
            updateHeight()
        }
    var resolved = false
    private var viewWidth = 0
    private var cells = mutableListOf<MutableList<Cell>>()
    private var _height = PADDING * 2
    val height: Int get() = _height
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

    }

    private fun updateHeight() {
        computeCells()
        val margins = if (cells.size == 0) 0 else (cells.size - 1) * MARGIN_V
        _height = cells.size * HEIGHT + PADDING * 2 + margins
    }

    private fun computeCells() {
        cells = mutableListOf()
        if (word.isNotEmpty()) {
            val drawingWidth = viewWidth - PADDING * 2
            var x = drawingWidth
            var y = 0
            for (i in word.indices) {
                if (x > drawingWidth - WIDTH) {
                    cells.add(mutableListOf())
                    x = WIDTH + MARGIN_H
                    y += HEIGHT + MARGIN_V
                } else {
                    x += WIDTH + MARGIN_H
                }
                val value = if (resolved) word[i]?.value.toString() else null
                cells.last().add(
                    Cell(
                        x + PADDING - WIDTH - MARGIN_H,
                        y + PADDING - HEIGHT - MARGIN_V,
                        value
                    )
                )
            }
        }
    }

    companion object {
        const val PADDING = 10
        const val HEIGHT = 48
        const val WIDTH = 32
        const val MARGIN_H = 5
        const val MARGIN_V = 10
    }
}
