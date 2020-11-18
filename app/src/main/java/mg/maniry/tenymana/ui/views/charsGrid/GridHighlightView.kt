package mg.maniry.tenymana.ui.views.charsGrid

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import androidx.core.content.res.ResourcesCompat
import mg.maniry.tenymana.gameLogic.models.Point
import mg.maniry.tenymana.ui.game.colors.GameColors
import mg.maniry.tenymana.ui.views.animator.AnimatedView
import mg.maniry.tenymana.ui.views.animator.Animator
import java.util.*

class GridHighlightView : BaseCharGridView, AnimatedView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) :
            super(context, attributeSet, defStyleAttr)

    override val control = GridHighlightControl()
    override var animator: Animator? = null

    fun onAnimDurationChanged(duration: Double) {
        control.animDuration = duration
    }

    override fun onColorsChanged(colors: GameColors) {
        control.onColorChanged(ResourcesCompat.getColor(resources, colors.accent, null))
    }

    fun onValue(value: List<Point>?) {
        control.onValue(value, Date().time)
        if (value == null) {
            animator?.forget(this)
        } else {
            animator?.register(this)
        }
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

    override fun reRender() {
        invalidate()
    }

    override fun update() {
        invalidate()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        super.onDispose()
    }
}
