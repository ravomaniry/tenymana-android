package mg.maniry.tenymana.ui.views.hiddenWords

import android.graphics.Canvas
import android.graphics.Paint
import mg.maniry.tenymana.gameLogic.models.Character

abstract class BaseHiddenWordsViewControl {
    data class Cell(
        val x: Float,
        val y: Float,
        val value: String?
    )

    protected abstract val padding: Float
    protected abstract val cellWidth: Float
    protected abstract val cellHeight: Float
    protected abstract val marginV: Float
    protected abstract val marginH: Float

    protected abstract fun cellValueAccessor(character: Character?): String?

    abstract fun draw(canvas: Canvas)

    var word: List<Character?> = emptyList()
        set(value) {
            if (field != value) {
                field = value
                updateHeight()
            }
        }

    private var viewWidth = 0
    protected var cells = listOf<List<Cell>>()
    private var _height = 10f
    val height: Int get() = _height.toInt()
    private var t0 = 0L
    private var animValue = 0.0
    private var prevAnimValue = 1.0

    fun onMeasure(width: Int) {
        viewWidth = width
        updateHeight()
    }

    fun startAnim(t: Long) {
        t0 = t
        animValue = 0.0
    }

    fun onTick(t: Long): Boolean {
        val dt = (t - t0)
        animValue = if (dt < ANIM_DURATION) dt / ANIM_DURATION.toDouble() else 1.0
        val shouldUpdate = animValue != prevAnimValue
        prevAnimValue = animValue
        return shouldUpdate
    }

    protected fun Canvas.drawCellBg(x: Float, y: Float, width: Float, height: Float, paint: Paint) {
        val xToUse = applyAnimOffset(x)
        drawRect(xToUse, y, xToUse + width, y + height, paint)
    }

    protected fun Canvas.drawCellText(x: Float, y: Float, value: String, paint: Paint) {
        val xToUse = applyAnimOffset(x)
        drawText(value, xToUse, y, paint)
    }

    private fun applyAnimOffset(value: Float): Float {
        return (value * animValue).toFloat()
    }

    private fun updateHeight() {
        updateCells()
        val margins = if (cells.isEmpty()) 0f else (cells.size - 1) * marginV
        _height = cells.size * cellHeight + padding * 2 + margins
    }

    protected fun updateCells() {
        val next = calcLayout()
        next.center(viewWidth, cellWidth, marginH)
        cells = next
    }

    private fun calcLayout(): MutableList<MutableList<Cell>> {
        val cells: MutableList<MutableList<Cell>> = mutableListOf()
        if (word.isNotEmpty()) {
            val drawingWidth = viewWidth - padding * 2
            var x = drawingWidth + 1
            var y = 0f
            for (char in word) {
                if (x + cellWidth > drawingWidth) {
                    cells.add(mutableListOf())
                    x = 0f
                    y += cellHeight + marginV
                }
                cells.last().add(
                    Cell(
                        x,
                        y + padding - cellHeight - marginV,
                        cellValueAccessor(char)
                    )
                )
                x += cellWidth + marginH
            }
        }
        return cells
    }

    private fun MutableList<MutableList<Cell>>.center(
        viewWidth: Int,
        cellWidth: Float,
        marginH: Float
    ) {
        for (row in this) {
            val w = row.size * cellWidth + (row.size - 1) * marginH
            val padding = (viewWidth - w) / 2
            for (i in row.indices) {
                row[i] = row[i].copy(x = row[i].x + padding)
            }
        }
    }

    companion object {
        const val ANIM_DURATION = 250L
    }
}
