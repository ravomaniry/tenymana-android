package mg.maniry.tenymana.ui.game.puzzle.views

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import mg.maniry.tenymana.gameLogic.models.Character
import mg.maniry.tenymana.gameLogic.models.Grid
import mg.maniry.tenymana.gameLogic.models.Point
import kotlin.math.floor
import kotlin.math.min

class CharGridViewControl {
    var settings: DrawingSettings? = null
    private var grid: Grid<Character>? = null
    private var boardHeight = 0
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

    fun onColorChanged(primary: Int) {
        bgPaint.color = primary
    }

    private fun updateDrawingSettings() {
        if (settings != null && grid != null && boardHeight > 0 && w > 0 && h > 0) {
            val cellSize = floor(min(w.toDouble() / grid!!.w, h.toDouble() / boardHeight)).toFloat()
            val totalW = cellSize * grid!!.w
            val x0 = (w - totalW) / 2
            textPaint.textSize = cellSize
            settings!!.charGridOrigin = Point(x0.toInt(), h.toInt())
            settings!!.charGridCellSize = cellSize
        }
    }

    fun draw(canvas: Canvas, mode: DrawMode) {
        if (grid != null) {
            for (y in 0 until boardHeight) {
                for (x in 0 until grid!!.w) {
                    val char = grid!![x, y]
                    if (char == null) {
                        if (mode == DrawMode.BG) {
                            drawBG(canvas, x, y, emptyBgPaint)
                        }
                    } else {
                        if (mode == DrawMode.BG) {
                            drawBG(canvas, x, y, bgPaint)
                        } else {
                            drawChar(canvas, char.value, x, y)
                        }
                    }
                }
            }
        }
    }

    private fun drawBG(canvas: Canvas, x: Int, y: Int, paint: Paint) {
        val left = calcLeft(x)
        val top = calcTop(y)
        canvas.drawRect(left, top, left + cellSize - MARGIN, top + cellSize - MARGIN, paint)
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

    enum class DrawMode {
        BG,
        TEXT
    }

    companion object {
        const val MARGIN = 2
        val emptyBgPaint = Paint().apply {
            style = Paint.Style.FILL
            color = Color.argb(50, 0, 0, 0)
        }
    }
}
