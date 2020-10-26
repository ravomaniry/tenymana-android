package mg.maniry.tenymana.ui.game.puzzle.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import mg.maniry.tenymana.gameLogic.models.Character
import mg.maniry.tenymana.gameLogic.models.Grid
import mg.maniry.tenymana.gameLogic.models.Point
import mg.maniry.tenymana.ui.game.colors.GameColors
import kotlin.math.floor
import kotlin.math.min

class CharGridView : View {
    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) :
            super(context, attributeSet, defStyleAttr)

    private val brain = CharGridViewBrain()

    fun onGridChanged(grid: Grid<Character>) {
        brain.onGridChanged(grid)
        invalidate()
    }

    fun onVisibleHChanged(h: Int) {
        brain.onVisibleHChanged(h)
        invalidate()
    }

    fun onColorsChanged(colors: GameColors) {
        brain.onColorChanged(ContextCompat.getColor(context, colors.primary))
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        brain.onSizeChanged(w, h)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas != null) {
            brain.draw(canvas)
        }
    }
}

class CharGridViewBrain {
    companion object {
        const val MARGIN = 2
    }

    private var origin = Point(0, 0)
    private var grid: Grid<Character>? = null
    private var visibleH = 0
    private var w = 0f
    private var h = 0f
    private var cellSize = 0f
    private val bgPaint = Paint().apply {
        style = Paint.Style.FILL
    }
    private val textPaint = Paint().apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        color = Color.WHITE
    }
    private val textDY: Float get() = cellSize - MARGIN * 4
    private val textDX: Float get() = (cellSize - MARGIN) / 2

    fun onSizeChanged(w: Int, h: Int) {
        this.w = w.toFloat()
        this.h = h.toFloat()
        updateDrawingSettings()
    }

    fun onGridChanged(grid: Grid<Character>) {
        this.grid = grid
        updateDrawingSettings()
    }

    fun onVisibleHChanged(h: Int) {
        this.visibleH = h
        updateDrawingSettings()
    }

    fun onColorChanged(primary: Int) {
        bgPaint.color = primary
    }

    private fun updateDrawingSettings() {
        if (grid != null && visibleH > 0 && w > 0 && h > 0) {
            cellSize = floor(min(w.toDouble() / grid!!.w, h.toDouble() / visibleH)).toFloat()
            val totalW = cellSize * grid!!.w
            val x0 = (w - totalW) / 2
            origin = Point(x0.toInt(), h.toInt())
            textPaint.textSize = cellSize
        }
    }

    fun draw(canvas: Canvas) {
        grid?.forEach { x, y, char ->
            if (char != null) {
                drawBG(canvas, x, y)
                drawChar(canvas, char.value, x, y)
            }
        }
    }

    private fun drawBG(canvas: Canvas, x: Int, y: Int) {
        val left = calcLeft(x)
        val top = calcTop(y)
        canvas.drawRect(left, top, left + cellSize - MARGIN, top + cellSize - MARGIN, bgPaint)
    }

    private fun drawChar(canvas: Canvas, char: Char, x: Int, y: Int) {
        canvas.drawText(char.toString(), calcLeft(x) + textDX, calcTop(y) + textDY, textPaint)
    }

    private fun calcLeft(x: Int): Float {
        return cellSize * x + origin.x
    }

    private fun calcTop(y: Int): Float {
        return origin.y - (y * cellSize) - cellSize - MARGIN
    }
}
