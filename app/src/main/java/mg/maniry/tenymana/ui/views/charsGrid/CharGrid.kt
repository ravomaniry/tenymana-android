package mg.maniry.tenymana.ui.views.charsGrid

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import mg.maniry.tenymana.gameLogic.models.Move
import mg.maniry.tenymana.ui.views.animator.AnimatedView
import mg.maniry.tenymana.ui.views.animator.Animator
import java.util.*

class CharGrid : BaseCharGridView, AnimatedView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) :
            super(context, attributeSet, defStyleAttr)

    override val control: CharGridControl = CharGridControl()
    override var animator: Animator? = null
    var shouldInvalidate = false

    fun onDiffs(values: List<Move>?) {
        if (values == null) {
            animator?.forget(this)
        } else {
            animator?.register(this)
        }
        control.onDiffs(values, Date().time)
    }

    override fun onDraw(canvas: Canvas?) {
        if (canvas != null) {
            control.draw(canvas)
        }
    }

    override fun update() {
        invalidate()
    }

    override fun onFrame() {
        invalidate()
    }

    override fun onTick(t: Long): Boolean {
        if (shouldInvalidate) {
            invalidate()
        }
        shouldInvalidate = control.onTick(t)
        return shouldInvalidate
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        onDispose()
    }
}
