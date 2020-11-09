package mg.maniry.tenymana.ui.views.charsGrid

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.res.ResourcesCompat
import mg.maniry.tenymana.gameLogic.models.Point
import mg.maniry.tenymana.ui.game.colors.GameColors
import mg.maniry.tenymana.ui.views.DrawingSettings
import java.util.*

class GridHighlightView : View {
    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) :
            super(context, attributeSet, defStyleAttr)

    private val animator = ValueAnimator.ofFloat(0f, 1f)
    private val control = GridHighlightControl(animator)

    fun onAnimDurationChanged(duration: Double) {
        control.animDuration = duration
    }

    fun onSettingsChanged(settings: DrawingSettings) {
        control.settings = settings
    }

    fun onValue(value: List<Point>?) {
        control.onValue(value, Date().time)
    }

    fun onColor(value: GameColors) {
        control.onColor(ResourcesCompat.getColor(resources, value.accent, null))
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas != null) {
            control.onDraw(canvas)
        }
    }

    init {
        animator.addUpdateListener {
            val reRender = control.onTick(Date().time)
            if (reRender) {
                invalidate()
            }
        }
    }
}

class GridHighlightControl(
    private val animator: ValueAnimator
) {
    enum class Mode { GROW, IDLE, SHRINK }

    var t0: Long = 0
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
    }

    var animDuration = 500.0
        set(value) {
            field = value
            animator.duration = value.toLong()
        }

    fun onColor(color: Int) {
        paint.color = color
    }

    fun onValue(value: List<Point>?, now: Long) {
        t0 = now
        this.value = value
        stopAnimator()
        if (value != null) {
            calcStarts()
            initSizes()
            initCenters()
            animator.start()
        }
    }

    fun onTick(now: Long): Boolean {
        this.t = (now - t0) / animDuration
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
