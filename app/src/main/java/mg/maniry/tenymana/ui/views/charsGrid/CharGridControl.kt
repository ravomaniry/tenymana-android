package mg.maniry.tenymana.ui.views.charsGrid

import android.graphics.Canvas
import mg.maniry.tenymana.gameLogic.models.Move
import mg.maniry.tenymana.gameLogic.models.Point
import mg.maniry.tenymana.ui.views.settings.DrawingSettings

class CharGridControl : BaseCharGridControl() {
    private var t0 = 0L
    private var t = 0.0
    private var offsets: Map<Point, Point> = hashMapOf()
    private var animate = false

    fun onDiffs(values: List<Move>?, now: Long) {
        if (values == null) {
            animate = false
        } else if (settings != null) {
            t0 = now
            animate = true
            offsets = values.toOffsetsMap(settings!!)
        }
    }

    fun onTick(now: Long): Boolean {
        t = (now - t0).toDouble() / animDuration
        animate = t < 1
        if (!animate) {
            offsets = hashMapOf()
        }
        return animate
    }

    override fun draw(canvas: Canvas) {
        if (grid != null) {
            val ys = 0 until boardHeight
            val xs = 0 until grid!!.w
            for (y in ys) {
                for (x in xs) {
                    if (grid!![x, y] == null) {
                        drawEmptyBG(canvas, x, y)
                    }
                }
            }
            for (y in ys) {
                for (x in xs) {
                    val char = grid!![x, y]
                    if (char != null) {
                        val offset = offsets[Point(x, y)]
                        val xOffset = offset?.x.toOffset(t)
                        val yOffset = offset?.y.toOffset(t)
                        drawFilledBG(canvas, x, y, xOffset, yOffset)
                        drawChar(canvas, char.value, x, y, xOffset, yOffset)
                    }
                }
            }
        }
    }

    companion object {
        const val animDuration = 300L
    }
}

private fun List<Move>.toOffsetsMap(settings: DrawingSettings): Map<Point, Point> {
    val offsets = hashMapOf<Point, Point>()
    forEach {
        val d = Point(it.a.x - it.b.x, it.b.y - it.a.y)
        offsets[it.b] = d * settings.charGridCellSize.toInt()
    }
    return offsets
}

private fun Int?.toOffset(t: Double): Float {
    val fValue = this?.toFloat() ?: 0f
    if (this == null) {
        return fValue
    }
    return (fValue - fValue * t).toFloat()
}
