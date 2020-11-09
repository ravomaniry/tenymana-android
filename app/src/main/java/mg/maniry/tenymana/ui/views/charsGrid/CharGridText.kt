package mg.maniry.tenymana.ui.views.charsGrid

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet

class CharGridText : BaseCharGridView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) :
            super(context, attributeSet, defStyleAttr)

    override val control = CharGridViewControl()

    override fun onDraw(canvas: Canvas?) {
        if (canvas != null) {
            control.draw(canvas, CharGridViewControl.DrawMode.TEXT)
        }
    }
}