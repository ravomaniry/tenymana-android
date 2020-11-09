package mg.maniry.tenymana.ui.views.charsGrid

import android.graphics.Canvas
import android.graphics.Paint
import mg.maniry.tenymana.gameLogic.models.Point
import mg.maniry.tenymana.ui.views.DrawingSettings

class GridHighlightControl {
    enum class Mode { GROW, IDLE, SHRINK }

    var settings: DrawingSettings? = null
    private var t: Double = 0.0
    private var t0: Long = 0
    private var sizes: MutableList<Float> = mutableListOf()
    private var centers: List<Point> = mutableListOf()
    private var tZeroes: MutableList<Double> = mutableListOf()
    private var mode: Mode? = null
    private var value: List<Point>? = null
    private val cellSize: Float get() = settings?.charGridCellSize ?: 0f
    private val paint = Paint().apply {
        style = Paint.Style.FILL
    }

    var animDuration = 500.0

    fun onColor(color: Int) {
        paint.color = color
    }

    fun onValue(value: List<Point>?, now: Long) {
        t0 = now
        this.value = value
        if (value != null) {
            calcStarts()
            initSizes()
            initCenters()
        }
    }

    fun onTick(now: Long): Boolean {
        this.t = (now - t0) / animDuration
        mode = when {
            t < .5 -> Mode.GROW
            t <= 0.8 -> Mode.IDLE
            t < 1 -> Mode.SHRINK
            else -> null
        }
        return mode != null
    }

    fun onDraw(canvas: Canvas) {
        if (settings != null) {
            when (mode) {
                Mode.GROW -> handleSizeGrow()
                Mode.IDLE -> handleSizeIdle()
                Mode.SHRINK -> handleSizeShrink()
            }
            if (mode != null) {
                draw(canvas)
            }
        }
    }

    private fun calcStarts() {
        tZeroes = mutableListOf()
        val interval = maxDelay / (value!!.size - 1)
        for (i in value!!.indices) {
            tZeroes.add(i * interval)
        }
    }

    private fun initSizes() {
        sizes = MutableList(value!!.size) { 0f }
    }

    private fun initCenters() {
        val origin = settings?.charGridOrigin
        if (origin != null) {
            centers = value!!.map {
                val x = origin.x + (it.x.toFloat() + 0.5) * cellSize
                val y = origin.y - (it.y.toFloat() + 0.5) * cellSize
                Point(x.toInt(), y.toInt())
            }
        }
    }

    private fun handleSizeGrow() {
        for (i in sizes.indices) {
            val t0 = tZeroes[i]
            sizes[i] = (cellSize * (t - t0) / (maxGrow - t0)).toFloat()
        }
    }

    private fun handleSizeIdle() {
        for (i in sizes.indices) {
            sizes[i] = cellSize
        }
    }

    private fun handleSizeShrink() {
        for (i in sizes.indices) {
            sizes[i] = cellSize - (cellSize * (t - shrink0) / (1 - shrink0)).toFloat()
        }
    }

    private fun draw(canvas: Canvas) {
        for (i in sizes.indices) {
            if (sizes[i] > 0) {
                canvas.drawRect(
                    centers[i].x - sizes[i] / 2,
                    centers[i].y - sizes[i] / 2,
                    centers[i].x + sizes[i] / 2,
                    centers[i].y + sizes[i] / 2,
                    paint
                )
            }
        }
    }

    companion object {
        const val maxDelay = 0.25
        const val maxGrow = 0.5
        const val shrink0 = 0.8
    }
}