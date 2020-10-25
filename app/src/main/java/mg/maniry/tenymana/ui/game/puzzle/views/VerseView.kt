package mg.maniry.tenymana.ui.game.puzzle.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Build
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import mg.maniry.tenymana.gameLogic.models.CharAddress
import mg.maniry.tenymana.gameLogic.models.Character
import mg.maniry.tenymana.gameLogic.models.Word
import mg.maniry.tenymana.ui.game.colors.GameColors

class VerseView : View {
    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) :
            super(context, attributeSet, defStyleAttr)

    private val brain = VerseViewBrain()

    fun onWordsChange(words: List<Word>?) {
        brain.onWordsChange(words)
        requestLayout()
    }

    fun onColorsChanged(colors: GameColors) {
        brain.onColorsChange(
            ContextCompat.getColor(context, colors.primary),
            ContextCompat.getColor(context, colors.accent)
        )
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val w = MeasureSpec.getSize(widthMeasureSpec)
        brain.onMeasure(w)
        setMeasuredDimension(w, brain.height)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas != null) {
            brain.draw(canvas, Build.VERSION.SDK_INT)
        }
    }
}

class VerseViewBrain {
    private var width = 0
    private var words: List<Word>? = null
    private var cells: List<List<Cell>> = listOf()
    private var primaryColor: Int = 0
    private var accentColor: Int = 0

    private val placeHolderPaint = Paint().apply { style = Paint.Style.FILL }
    private val charPaint = Paint().apply { style = Paint.Style.FILL }

    val height: Int get() = (H + SPACING_V) * cells.size + SPACING_V

    fun onMeasure(w: Int) {
        width = w
        computeCells()
    }

    fun onWordsChange(words: List<Word>?) {
        this.words = words
        computeCells()
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
            var y = SPACING_V.toFloat()
            next.add(mutableListOf())
            words!!.forEach {
                val w = it.width
                val totalW = currentW + w
                if (totalW <= width) {
                    currentW = totalW
                } else {
                    rowI++
                    y += H + SPACING_V
                    currentW = w
                    next.add(mutableListOf())
                }
                next[rowI].append(it, y)
            }
        }
    }

    private fun updatePaints() {
        placeHolderPaint.color = primaryColor
        charPaint.color = accentColor
    }

    fun draw(canvas: Canvas, sdkVersion: Int) {
        for (row in cells) {
            for (cell in row) {
                val w = words!![cell.char.wIndex]
                val char = w[cell.char.cIndex]
                if (w.resolved || char.resolved) {
                    drawChar(canvas, cell, char)
                } else {
                    drawPlaceHolder(canvas, cell, sdkVersion)
                }
            }
        }
    }

    private fun drawPlaceHolder(canvas: Canvas, cell: Cell, sdkVersion: Int) {
        if (sdkVersion >= 21) {
            @Suppress("newApi")
            canvas.drawRoundRect(
                cell.x,
                cell.y,
                cell.x + W,
                cell.y + H,
                BORDER_R,
                BORDER_R,
                placeHolderPaint
            )
        } else {
            canvas.drawRect(cell.x, cell.y, cell.x + W, cell.y + H, placeHolderPaint)
        }
    }

    private fun drawChar(canvas: Canvas, cell: Cell, char: Character) {
        canvas.drawText(char.value.toString(), cell.x, cell.y, charPaint)
    }

    companion object {
        const val W = 16
        const val H = 20
        const val SPACING_H = 4
        const val SPACING_V = 5
        const val BORDER_R = 4f
    }
}

private data class Cell(
    val x: Float,
    val y: Float,
    val char: CharAddress
)

private val Word.width: Int
    get() {
        if (isSeparator) {
            return size * VerseViewBrain.W
        }
        var w = 0
        for (i in 0 until size) {
            w += VerseViewBrain.W
            if (i != size - 1) {
                w += VerseViewBrain.SPACING_H
            }
        }
        return w
    }

private fun MutableList<Cell>.append(word: Word, y: Float) {
    var x = 0f
    for (i in 0 until word.size) {
        add(Cell(x, y, CharAddress(word.index, i)))
        x += VerseViewBrain.W + VerseViewBrain.SPACING_H
    }
}
