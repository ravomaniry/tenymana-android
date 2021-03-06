package mg.maniry.tenymana.ui.views.charsGrid

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import mg.maniry.tenymana.gameLogic.models.Character
import mg.maniry.tenymana.gameLogic.models.Grid
import mg.maniry.tenymana.gameLogic.models.Point
import mg.maniry.tenymana.ui.views.settings.DrawingSettings
import kotlin.math.floor
import kotlin.math.min

abstract class BaseCharGridControl {
    open var settings: DrawingSettings? = null
    protected var grid: Grid<Character>? = null
    protected var boardHeight = 0
    private var w = 0f
    private var h = 0f
    private val bgPaint = Paint().apply {
        style = Paint.Style.FILL
    }
    private val textPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        color = Color.WHITE
    }
    private val cellSize: Float get() = settings?.charGridCellSize ?: 0f
    private val origin: Point get() = settings?.charGridOrigin ?: Point(0, 0)
    private val textDY: Float get() = cellSize - MARGIN * 4
    private val textDX: Float get() = (cellSize - MARGIN) / 2

    abstract fun draw(canvas: Canvas)

    fun onSizeChanged(w: Int, h: Int) {
        this.w = w.toFloat()
        this.h = h.toFloat()
        updateDrawingSettings()
    }

    fun onGridChanged(grid: Grid<Character>?) {
        this.grid = grid
        updateDrawingSettings()
    }

    fun onVisibleHChanged(h: Int) {
        this.boardHeight = h
        updateDrawingSettings()
    }

    open fun onColorChanged(color: Int) {
        bgPaint.color = color
    }

    private fun updateDrawingSettings() {
        if (settings != null && grid != null && boardHeight > 0 && w > 0 && h > 0) {
            val cellSize = floor(min(w.toDouble() / grid!!.w, h.toDouble() / boardHeight)).toFloat()
            val totalW = cellSize * grid!!.w
            val x0 = (w - totalW) / 2
            settings!!.charGridOrigin = Point(x0.toInt(), h.toInt())
            settings!!.charGridCellSize = cellSize
            updateTextSize()
        }
    }

    protected fun updateTextSize() {
        if (settings != null) {
            textPaint.textSize = settings!!.charGridCellSize
        }
    }

    protected fun drawFilledBG(
        canvas: Canvas,
        x: Int,
        y: Int,
        xOffset: Float = 0f,
        yOffset: Float = 0f,
        size: Float = cellSize - MARGIN
    ) {
        drawBG(canvas, x, y, xOffset, yOffset, size, bgPaint)
    }

    protected fun drawEmptyBG(
        canvas: Canvas,
        x: Int,
        y: Int,
        xOffset: Float = 0f,
        yOffset: Float = 0f
    ) {
        drawBG(canvas, x, y, xOffset, yOffset, paint = emptyBgPaint)
    }

    private fun drawBG(
        canvas: Canvas,
        x: Int,
        y: Int,
        xOffset: Float,
        yOffset: Float,
        size: Float = cellSize - MARGIN,
        paint: Paint
    ) {
        val left = calcLeft(x) + xOffset
        val top = calcTop(y) + yOffset
        canvas.drawRect(left, top, left + size, top + size, paint)
    }

    protected fun drawCharAt(canvas: Canvas, x: Int, y: Int) {
        val char = grid?.get(x, y)
        if (char != null) {
            drawChar(canvas, char.value, x, y)
        }
    }

    protected fun drawChar(
        canvas: Canvas,
        char: Char,
        x: Int,
        y: Int,
        xOffset: Float = 0f,
        yOffset: Float = 0f
    ) {
        val left = xOffset + calcLeft(x) + textDX
        val top = yOffset + calcTop(y) + textDY
        canvas.drawText(char.toString(), left, top, textPaint)
    }

    private fun calcLeft(x: Int): Float {
        return cellSize * x + origin.x
    }

    private fun calcTop(y: Int): Float {
        return origin.y - (y * cellSize) - cellSize + MARGIN
    }

    companion object {
        const val MARGIN = 2
        val emptyBgPaint = Paint().apply {
            style = Paint.Style.FILL
            color = Color.argb(128, 255, 255, 255)
        }
    }
}
