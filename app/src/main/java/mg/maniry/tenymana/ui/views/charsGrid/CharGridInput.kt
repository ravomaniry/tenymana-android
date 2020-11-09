package mg.maniry.tenymana.ui.views.charsGrid

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.core.content.ContextCompat
import mg.maniry.tenymana.gameLogic.models.Move
import mg.maniry.tenymana.ui.game.colors.GameColors

typealias ProposeFn = (Move) -> Unit

class CharGridInput : BaseCharGridView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) :
            super(context, attributeSet, defStyleAttr)

    override val control = CharGridInputControl()

    override fun onColorsChanged(colors: GameColors) {
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
