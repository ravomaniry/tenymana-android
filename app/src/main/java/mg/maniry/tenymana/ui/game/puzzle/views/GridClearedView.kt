package mg.maniry.tenymana.ui.game.puzzle.views

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import mg.maniry.tenymana.gameLogic.models.Point
import java.util.*

class GridClearedView : View {
    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) :
            super(context, attributeSet, defStyleAttr)

    private val animator = ValueAnimator.ofFloat(0f, 1f).apply {
        duration = animDuration
    }
    private val control = GridClearedViewControl(animator)

    fun onSettingsChanged(settings: DrawingSettings) {
        control.settings = settings
    }

    fun onValue(value: List<Point>?) {
        control.onValue(value)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas != null) {
            control.onDraw(canvas)
        }
    }

    init {
        val t0 = Date().time
        animator.addUpdateListener {
            val t = (Date().time - t0).toDouble() / animDuration
            val reRender = control.onTick(t)
            if (reRender) {
                invalidate()
            }
        }
    }

    companion object {
        const val animDuration = 500L
    }
}

class GridClearedViewControl(
    private val animator: ValueAnimator
) {
    enum class Mode { GROW, IDLE, SHRINK }

    var t: Double = 0.0
    var settings: DrawingSettings? = null
    private var sizes: MutableList<Float> = mutableListOf()
    private var centers: List<Point> = mutableListOf()
    private var tZeroes: MutableList<Double> = mutableListOf()
    private var mode: Mode? = null
    private var value: List<Point>? = null
    private val cellSize: Float get() = settings?.charGridCellSize ?: 0f
    private val paint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.BLUE
    }

    fun onValue(value: List<Point>?) {
        this.value = value
        stopAnimator()
        if (value != null) {
            calcStarts()
            initSizes()
            initCenters()
            animator.start()
        }
    }

    fun onTick(t: Double): Boolean {
        this.t = t
        val nextMode = when {
            t < .5 -> Mode.GROW
            t <= 0.8 -> Mode.IDLE
            t < 1 -> Mode.SHRINK
            else -> null
        }
        if ((mode == Mode.IDLE && nextMode == Mode.IDLE) || mode == null && nextMode == null) {
            return false
        }
        mode = nextMode
        return true
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

    private fun stopAnimator() {
        if (animator.isRunning) {
            animator.cancel()
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
