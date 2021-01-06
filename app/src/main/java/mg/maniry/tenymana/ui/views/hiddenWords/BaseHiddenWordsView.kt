package mg.maniry.tenymana.ui.views.hiddenWords

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import mg.maniry.tenymana.gameLogic.models.Character
import mg.maniry.tenymana.ui.game.colors.GameColors
import mg.maniry.tenymana.ui.views.animator.AnimatedView
import mg.maniry.tenymana.ui.views.animator.Animator
import java.util.*

abstract class BaseHiddenWordsView : AppCompatTextView, AnimatedView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) :
            super(context, attributeSet, defStyleAttr)

    protected abstract val control: BaseHiddenWordsViewControl

    override var animator: Animator? = null

    abstract fun onColorsChange(colors: GameColors)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        control.onMeasure(width)
        setMeasuredDimension(width, control.height)
    }

    fun onWordChange(word: List<Character?>) {
        if (control.word != word) {
            val prevH = control.height
            control.word = word
            if (prevH == control.height) {
                invalidate()
            } else {
                invalidate()
                requestLayout()
            }
        }
    }

    fun startAnim() {
        control.startAnim(Date().time)
        animator?.register(this)
    }

    override fun onTick(t: Long): Boolean {
        return control.onTick(t)
    }

    override fun onFrame() {
        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas != null) {
            control.draw(canvas)
        }
    }
}
