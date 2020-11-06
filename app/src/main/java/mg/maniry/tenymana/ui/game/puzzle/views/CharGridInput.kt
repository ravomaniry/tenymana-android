package mg.maniry.tenymana.ui.game.puzzle.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import mg.maniry.tenymana.gameLogic.models.Character
import mg.maniry.tenymana.gameLogic.models.Grid
import mg.maniry.tenymana.gameLogic.models.Move
import mg.maniry.tenymana.gameLogic.models.Point
import mg.maniry.tenymana.ui.game.colors.GameColors
import kotlin.math.floor

typealias ProposeFn = (Move) -> Unit

class CharGridInput : View {
    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) :
            super(context, attributeSet, defStyleAttr)

    private val control = CharGridInputControl()

    fun onSettingsChanged(settings: DrawingSettings) {
        control.settings?.forget(DrawingSettings.Event.CHAR_GRID, this)
        control.settings = settings
        settings.subscribe(DrawingSettings.Event.CHAR_GRID, this)
    }

    fun onGridChanged(grid: Grid<Character>?) {
        control.onGridChanged(grid)
    }

    fun onColorsChanged(colors: GameColors) {
        control.onColorChanged(ContextCompat.getColor(context, colors.accent))
    }

    fun onPropose(fn: ProposeFn?) {
        control.propose = fn
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas != null) {
            control.draw(canvas)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event != null) {
            control.onTouch(event)
            invalidate()
            if (event.action == MotionEvent.ACTION_UP) {
                performClick()
            }
        }
        return true
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }
}

class CharGridInputControl {
    var settings: DrawingSettings? = null
    var propose: ProposeFn? = null
    private var start: Point? = null
    private var end: Point? = null
    private var gridWidth = 0
    private val linePaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = 8f
    }

    fun onGridChanged(grid: Grid<Character>?) {
        gridWidth = grid?.w ?: 0
    }

    fun onColorChanged(color: Int) {
        linePaint.color = color
    }

    fun draw(canvas: Canvas) {
        if (start != null && end != start) {
            canvas.joinPoints(start!!, end!!, linePaint)
        }
    }

    fun onTouch(e: MotionEvent) {
        val x = e.x.toInt()
        val y = e.y.toInt()
        if (e.action == MotionEvent.ACTION_UP) {
            handleEnd(x, y)
        } else {
            handleStart(x, y)
            handleMove(x, y)
        }
    }

    private fun handleStart(x: Int, y: Int) {
        if (start == null) {
            start = Point(x, y)
        }
    }

    private fun handleMove(x: Int, y: Int) {
        end = Point(x, y)
    }

    private fun handleEnd(x: Int, y: Int) {
        end = Point(x, y)
        submit()
    }

    private fun submit() {
        if (settings != null && start != null && end != null) {
            val a = start!!.toGridCoordinate(settings!!)
            val b = end!!.toGridCoordinate(settings!!)
            propose?.invoke(Move(a, b))
        }
        start = null
        end = null
    }
}

private fun Canvas.joinPoints(a: Point, b: Point, paint: Paint) {
    drawLine(a.x.toFloat(), a.y.toFloat(), b.x.toFloat(), b.y.toFloat(), paint)
}

private fun Point.toGridCoordinate(settings: DrawingSettings): Point {
    val dX = x - settings.charGridOrigin.x
    val dY = settings.charGridOrigin.y - y
    return Point(
        floor(dX / settings.charGridCellSize).toInt(),
        floor(dY / settings.charGridCellSize).toInt()
    )
}
