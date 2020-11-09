package mg.maniry.tenymana.ui.views.charsGrid

import android.graphics.Canvas
import android.view.MotionEvent
import mg.maniry.tenymana.gameLogic.models.Move
import mg.maniry.tenymana.gameLogic.models.Point
import mg.maniry.tenymana.ui.views.DrawingSettings
import kotlin.math.floor

class CharGridInputControl : BaseCharGridControl() {
    var propose: ProposeFn? = null
    private var start: Point? = null
    private var end: Point? = null
    private var highlights: MutableSet<Point> = mutableSetOf()

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
            highlights = mutableSetOf(start!!.toGridCoordinate(settings!!))
        }
    }

    private fun handleMove(x: Int, y: Int) {
        val p = Point(x, y)
        end = p
        val gridP = p.toGridCoordinate(settings!!)
        if (!highlights.contains(gridP)) {
            highlights.add(gridP)
        }
    }

    private fun handleEnd(x: Int, y: Int) {
        end = Point(x, y)
        submit()
        highlights = mutableSetOf()
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

    override fun draw(canvas: Canvas) {
        for (p in highlights) {
            val char = grid?.get(p)
            if (char != null) {
                drawFilledBG(canvas, p.x, p.y)
                drawChar(canvas, char.value, p.x, p.y)
            }
        }
    }
}

private fun Point.toGridCoordinate(settings: DrawingSettings): Point {
    val dX = x - settings.charGridOrigin.x
    val dY = settings.charGridOrigin.y - y
    return Point(
        floor(dX / settings.charGridCellSize).toInt(),
        floor(dY / settings.charGridCellSize).toInt()
    )
}
