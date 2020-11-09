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

    abstract fun onTick(t: Long): Boolean

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        animator?.forget(this)
    }
}
