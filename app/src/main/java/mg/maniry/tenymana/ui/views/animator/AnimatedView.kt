package mg.maniry.tenymana.ui.views.animator

import android.content.Context
import android.util.AttributeSet
import android.view.View

abstract class AnimatedView : View {
    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) :
            super(context, attributeSet, defStyleAttr)

    var animator: Animator? = null
        set(value) {
            field = value
            listenAnimation()
        }

    abstract fun onTick(t: Long): Boolean

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        listenAnimation()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        forgetAnimation()
    }

    private fun listenAnimation() {
        animator?.register(this)
    }

    private fun forgetAnimation() {
        animator?.forget(this)
    }
}
