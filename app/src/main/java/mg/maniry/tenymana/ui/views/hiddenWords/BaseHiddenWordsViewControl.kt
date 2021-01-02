package mg.maniry.tenymana.ui.views.hiddenWords

import android.graphics.Canvas
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
            val shouldUpdate = field.size != value.size
            field = value
            if (shouldUpdate) {
                updateHeight()
            }
        }

    private var viewWidth = 0
    protected var cells = listOf<List<Cell>>()
    private var _height = 10f
    val height: Int get() = _height.toInt()

    fun onMeasure(width: Int) {
        viewWidth = width
        updateHeight()
    }

    private fun updateHeight() {
        updateCells()
        val margins = if (cells.isEmpty()) 0f else (cells.size - 1) * marginV
        _height = cells.size * cellHeight + padding * 2 + margins
    }

    private fun updateCells() {
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
}
