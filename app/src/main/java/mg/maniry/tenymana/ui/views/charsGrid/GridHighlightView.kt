package mg.maniry.tenymana.ui.views.charsGrid

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import androidx.core.content.res.ResourcesCompat
import mg.maniry.tenymana.gameLogic.models.Point
import mg.maniry.tenymana.ui.game.colors.GameColors
import mg.maniry.tenymana.ui.views.DrawingSettings
import mg.maniry.tenymana.ui.views.animator.AnimatedView
import java.util.*

class GridHighlightView : AnimatedView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) :
            super(context, attributeSet, defStyleAttr)

    private val control = GridHighlightControl()

    fun onAnimDurationChanged(duration: Double) {
        control.animDuration = duration
    }

    fun onSettingsChanged(settings: DrawingSettings) {
        control.settings = settings
    }

    fun onValue(value: List<Point>?) {
        control.onValue(value, Date().time)
        if (value == null) {
            animator?.forget(this)
        } else {
            animator?.register(this)
        }
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

    override fun onTick(t: Long): Boolean {
        val shouldInvalidate = control.onTick(t)
        if (!shouldInvalidate) {
            animator?.forget(this)
            invalidate()
        }
        return shouldInvalidate
    }
}
