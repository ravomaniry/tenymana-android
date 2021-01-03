package mg.maniry.tenymana.ui.views.hiddenWords

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.core.content.res.ResourcesCompat
import mg.maniry.tenymana.ui.game.colors.GameColors

class HiddenWordsInputView : BaseHiddenWordsView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) :
            super(context, attributeSet, defStyleAttr)

    override val control = HiddenWordsInputViewControl()
    private var clickX = 0f
    private var clickY = 0f

    fun onSelect(handler: SelectHandler) {
        control.onSelect(handler)
    }

    override fun onColorsChange(colors: GameColors) {
        val color = ResourcesCompat.getColor(resources, colors.primary, null)
        control.onColorChange(color)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event != null) {
            clickX = event.x
            clickY = event.y
            if (event.action == MotionEvent.ACTION_DOWN) {
                control.onClick(clickX.toInt(), clickY.toInt())
                performClick()
            }
        }
        return false
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }
}
