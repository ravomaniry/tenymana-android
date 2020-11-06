package mg.maniry.tenymana.ui.game.puzzle.views

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet

open class CharGridText : CharGridBackground {
    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) :
            super(context, attributeSet, defStyleAttr)

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas != null) {
            control.draw(canvas, CharGridViewControl.DrawMode.TEXT)
        }
    }
}
