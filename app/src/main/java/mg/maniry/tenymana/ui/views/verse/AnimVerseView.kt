package mg.maniry.tenymana.ui.views.verse

import android.content.Context
import android.util.AttributeSet
import mg.maniry.tenymana.ui.game.colors.GameColors
import mg.maniry.tenymana.ui.views.animator.AnimatedView
import mg.maniry.tenymana.ui.views.animator.Animator
import java.util.*

class AnimVerseView : BaseVerseView, AnimatedView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) :
            super(context, attributeSet, defStyleAttr)

    private var shouldInvalidate = false
    override var animator: Animator? = null
    override val control = AnimVerseViewControl()

    override fun onColorsChanged(colors: GameColors) {
        super.onColorsChanged(colors)
        invalidate()
    }

    override fun reRender() {
        control.startAnim(Date().time)
        invalidate()
    }

    override fun onTick(t: Long): Boolean {
        if (shouldInvalidate) {
            invalidate()
        }
        shouldInvalidate = control.onTick(Date().time)
        return shouldInvalidate
    }

    override fun onFrame() {
        invalidate()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        onDispose()
    }
}
